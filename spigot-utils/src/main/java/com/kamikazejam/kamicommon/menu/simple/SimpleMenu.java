package com.kamikazejam.kamicommon.menu.simple;

import com.kamikazejam.kamicommon.SpigotUtilsSource;
import com.kamikazejam.kamicommon.menu.Menu;
import com.kamikazejam.kamicommon.menu.api.MenuHolder;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.access.IMenuIconsAccess;
import com.kamikazejam.kamicommon.menu.api.icons.access.MenuIconsAccess;
import com.kamikazejam.kamicommon.menu.api.icons.interfaces.UpdatingMenu;
import com.kamikazejam.kamicommon.menu.api.icons.slots.IconSlot;
import com.kamikazejam.kamicommon.menu.api.icons.slots.StaticIconSlot;
import com.kamikazejam.kamicommon.menu.api.struct.MenuEvents;
import com.kamikazejam.kamicommon.menu.api.struct.MenuOptions;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSizeRows;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSizeType;
import com.kamikazejam.kamicommon.util.PlayerUtil;
import com.kamikazejam.kamicommon.util.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * This Menu class focuses on providing a simple single-frame menu. This is the most versatile menu type
 * because you define everything in the menu, and can create your own custom logic.
 */
@Getter
@Accessors(chain = true)
@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class SimpleMenu extends MenuHolder implements Menu, UpdatingMenu {
    // Fields
    private final Player player;
    private final Map<String, MenuIcon> menuIcons = new ConcurrentHashMap<>();
    private final MenuEvents events;
    private final MenuOptions options;
    // Internal Data
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private final AtomicInteger tickCounter = new AtomicInteger(0);

    // Constructor (Deep Copying from Builder)
    private SimpleMenu(@NotNull Builder builder, @NotNull Player player) {
        super(builder.size.copy(), builder.title);
        this.player = player;
        builder.menuIcons.forEach((id, icon) -> this.menuIcons.put(id, icon.copy()));
        this.events = builder.events.copy();
        this.options = builder.options.copy();
    }

    /**
     * Open the {@link Inventory} for the {@link Player} that this menu was created for.
     * @return The {@link InventoryView} for the new menu, or null if the player was not online to open the menu for.
     */
    @Nullable
    public InventoryView open() {
        if (!PlayerUtil.isFullyValidPlayer(player)) { return null; }

        // Reset the tick counter for icons that auto update if necessary
        if (options.isResetVisualsOnOpen()) {
            tickCounter.set(0);
        }

        // Place all icons into the inventory
        this.placeIcons(null);
        // After the initial placement of icons, run a fill
        // any slots with AIR that are left after normal icons are added to the filler's IconSlot
        this.fill();

        // Register this menu for auto-updating
        SpigotUtilsSource.getMenuManager().getAutoUpdateInventories().add(this);

        // Open the Menu for the Player
        InventoryView view = Objects.requireNonNull(player.openInventory(this.getInventory()));
        events.getOpenCallbacks().forEach(callback -> callback.onOpen(player, view));
        return view;
    }

    /**
     * Close the {@link Inventory} for the {@link Player} that this menu was created for.<br>
     * Identical to calling {@link Player#closeInventory()}.
     * @return If the inventory was successfully closed. False if the player is no longer valid (not online).
     */
    public boolean close() {
        if (!PlayerUtil.isFullyValidPlayer(player)) { return false; }

        player.closeInventory();
        return true;
    }

    // ------------------------------------------------------------ //
    //                        Icon Management                       //
    // ------------------------------------------------------------ //

    @NotNull
    public SimpleMenu modifyIcons(@NotNull Consumer<IMenuIconsAccess> consumer) {
        consumer.accept(new MenuIconsAccess(this.menuIcons));
        return this;
    }

    // ------------------------------------------------------------ //
    //                   Icon Update Management                     //
    // ------------------------------------------------------------ //
    @ApiStatus.Internal
    @Override
    public void updateOneTick() {
        // getAndIncrement means we start at 1, since 0th tick should have been the call to open()
        int tick = this.tickCounter.incrementAndGet();
        this.placeIcons((m) -> m.needsModification(tick));
    }

    /**
     * Manually trigger an update for all icons matching this predicate.<br>
     * If the predicate is passed, the icon will be re-built and set in the inventory.<br>
     * If the predicate is null, it will always update all icons.
     * @param filter An optional predicate to filter which icons are updated.
     */
    public void placeIcons(@Nullable Predicate<MenuIcon> filter) {
        this.menuIcons.values().forEach(icon -> this.placeIcon(filter, icon));
    }

    /**
     * Manually trigger an update for a specific icon.<br>
     * If the predicate is passed, the icon will be re-built and set in the inventory.<br>
     * If the predicate is null, it will always update the icon.
     * @param filter An optional predicate, that if failed, will not update the icon.
     * @param menuIcon The icon to update.
     */
    public void placeIcon(@Nullable Predicate<MenuIcon> filter, @NotNull MenuIcon menuIcon) {
        if (!menuIcon.isEnabled() || (filter != null && !filter.test(menuIcon))) { return; }
        int tick = this.tickCounter.get();

        @Nullable IconSlot iconSlot = menuIcon.getIconSlot();
        if (iconSlot == null) { return; }

        // Sort (deterministically) the slots to ensure the same ordering of the list, from the unordered set
        List<Integer> sortedSlots = new ArrayList<>(iconSlot.get(this));
        sortedSlots.sort(Integer::compareTo);
        if (sortedSlots.isEmpty()) { return; } // No work to do

        // Fetch the last (previous) ItemStack we placed for this icon (null if not placed yet)
        // This is used for any stateful MenuIcon modifiers that rely on the previous item state
        @Nullable ItemStack previousItem = menuIcon.getLastItem();

        // Calculate the new item (one time) for this icon & place it in all slots
        @Nullable ItemStack item = menuIcon.buildItem(tick > 0 && menuIcon.isCycleBuilderForTick(tick), previousItem);
        this.placeItemStack(item, menuIcon, iconSlot);

        // Store the new item as the previous item for the next update
        menuIcon.setLastItem(item);
    }

    private void placeItemStack(@Nullable ItemStack item, @NotNull MenuIcon menuIcon, @NotNull IconSlot iconSlot) {
        if (item != null && item.getAmount() > 64) { item.setAmount(64); }

        // Update the inventory slots
        int size = this.getSize();
        for (int slot : iconSlot.get(this)) {
            if (slot < 0 || slot >= size) { continue; }
            super.setItem(slot, item);
        }
    }

    private void fill() {
        @Nullable MenuIcon fillerIcon = this.menuIcons.getOrDefault("filler", null);
        if (fillerIcon == null || !fillerIcon.isEnabled()) { return; }

        // Find the slots that need to be filled
        List<Integer> slotsToFill = new ArrayList<>();
        for (int i = 0; i < getInventory().getSize(); i++) {
            if (this.options.getExcludedFillSlots().contains(i)) { continue; }
            ItemStack here = getInventory().getItem(i);
            if (here != null && here.getType() != Material.AIR) { continue; }

            slotsToFill.add(i);
        }
        // Update the filler's IconSlot value
        if (fillerIcon.getIconSlot() instanceof StaticIconSlot staticSlots) {
            // This will occur if we reopen the menu, and when we do we need to re-evaluate the slots
            List<Integer> newSlots = new ArrayList<>(slotsToFill);
            newSlots.addAll(staticSlots.getSlots());
            fillerIcon.setIconSlot(new StaticIconSlot(newSlots));
        }else {
            fillerIcon.setIconSlot(new StaticIconSlot(slotsToFill));
        }

        this.menuIcons.put("filler", fillerIcon);

        // Make sure the filler icon is placed in the Menu
        this.placeIcon(null, fillerIcon);
    }










    public static final class Builder {
        // Menu Details
        private @NotNull MenuSize size;
        private @Nullable String title;
        // Menu Icons
        private final Map<String, MenuIcon> menuIcons = new ConcurrentHashMap<>();
        // Additional Configuration
        private final MenuEvents events = new MenuEvents();
        private final MenuOptions options = new MenuOptions();

        public Builder(@NotNull MenuSize size) {
            Preconditions.checkNotNull(size, "Size must not be null.");
            this.size = size;
            // Add the default filler icon
            this.menuIcons.put("filler", MenuIcon.getDefaultFillerIcon());

        }
        public Builder(int rows) {
            this(new MenuSizeRows(rows));
        }
        public Builder(@NotNull InventoryType type) {
            this(new MenuSizeType(type));
        }

        @NotNull
        public Builder size(@NotNull MenuSize size) {
            Preconditions.checkNotNull(size, "Size must not be null.");
            this.size = size;
            return this;
        }

        @NotNull
        public Builder title(@Nullable String title) {
            this.title = title;
            return this;
        }

        @NotNull
        public Builder options(@NotNull MenuOptions.MenuOptionsModification modification) {
            Preconditions.checkNotNull(modification, "Modification must not be null.");
            modification.modify(this.options);
            return this;
        }

        @NotNull
        public Builder events(@NotNull MenuEvents.MenuEventsModification modification) {
            Preconditions.checkNotNull(modification, "Modification must not be null.");
            modification.modify(this.events);
            return this;
        }

        @NotNull
        public Builder fillerIcon(@Nullable MenuIcon fillerIcon) {
            if (fillerIcon == null) {
                this.menuIcons.remove("filler");
                return this;
            }
            fillerIcon.setId("filler");
            this.menuIcons.put("filler", fillerIcon);
            return this;
        }

        @NotNull
        public Builder modifyIcons(@NotNull Consumer<IMenuIconsAccess> consumer) {
            consumer.accept(new MenuIconsAccess(this.menuIcons));
            return this;
        }

        @NotNull
        @CheckReturnValue
        public SimpleMenu build(@NotNull Player player) {
            Preconditions.checkNotNull(player, "Player must not be null.");
            return new SimpleMenu(this, player);
        }
    }
}
