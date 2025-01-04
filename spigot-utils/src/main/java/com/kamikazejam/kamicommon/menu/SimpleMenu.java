package com.kamikazejam.kamicommon.menu;

import com.kamikazejam.kamicommon.SpigotUtilsSource;
import com.kamikazejam.kamicommon.menu.api.MenuHolder;
import com.kamikazejam.kamicommon.menu.api.title.MenuTitleCalculator;
import com.kamikazejam.kamicommon.menu.api.title.MenuTitleProvider;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.access.IMenuIconsAccess;
import com.kamikazejam.kamicommon.menu.api.icons.access.MenuIconsAccess;
import com.kamikazejam.kamicommon.menu.api.icons.interfaces.UpdatingMenu;
import com.kamikazejam.kamicommon.menu.api.struct.MenuEvents;
import com.kamikazejam.kamicommon.menu.api.struct.MenuOptions;
import com.kamikazejam.kamicommon.menu.api.struct.icons.PrioritizedMenuIconMap;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSizeRows;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSizeType;
import com.kamikazejam.kamicommon.menu.api.title.MenuTitleReplacement;
import com.kamikazejam.kamicommon.util.PlayerUtil;
import com.kamikazejam.kamicommon.util.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * This Menu class focuses on providing a simple single-frame menu. This is the most versatile menu type
 * because you define everything in the menu, and can create your own custom logic.
 */
@Getter
@Accessors(chain = true)
@SuppressWarnings({"unused", "unchecked", "UnusedReturnValue"})
public sealed class SimpleMenu<T extends SimpleMenu<T>> extends MenuHolder implements Menu, UpdatingMenu permits PaginatedMenu {
    // Fields
    private final Player player;
    // priority icon is used to keep track of the order icons were registered, which is necessary when resizing
    // The data type PriorityMenuIcon also keeps track of the slot data
    private final PrioritizedMenuIconMap menuIcons = new PrioritizedMenuIconMap();
    private final MenuEvents events;
    private final MenuOptions options;
    // Internal Data
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private final AtomicInteger tickCounter = new AtomicInteger(0);

    // Constructor (Deep Copying from Builder)
    SimpleMenu(@NotNull Builder<?> builder, @NotNull Player player) {
        super(builder.size.copy(), builder.titleCalculator.buildTitle(player));
        this.player = player;
        builder.menuIcons.values().forEach((pIcon) -> this.menuIcons.add(pIcon.copy()));
        this.events = builder.events.copy();
        this.options = builder.options.copy();
    }

    @Override
    public void reopenMenu(@NotNull Player player) {
        // Sanity Checks
        if (!PlayerUtil.isFullyValidPlayer(player) || !player.getUniqueId().equals(this.player.getUniqueId())) { return; }
        this.open();
    }

    /**
     * Open the {@link Inventory} for the {@link Player} that this menu was created for.
     * @return The {@link InventoryView} for the new menu, or null if the player was not online to open the menu for.
     */
    @Nullable
    public InventoryView open() {
        return this.openInternal(false);
    }

    private InventoryView openInternal(boolean isResizeCall) {
        if (!PlayerUtil.isFullyValidPlayer(player)) { return null; }

        // Reset the tick counter for icons that auto update if necessary
        if (!isResizeCall && options.isResetVisualsOnOpen()) {
            tickCounter.set(0);
        }

        // Place all icons into the inventory
        // This method will also handle the filler icon placement
        placeIcons(null);

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

    @Override
    public void setSize(@NotNull MenuSize size) {
        this.resizeMenu(size);
    }

    public void resizeMenu(@NotNull MenuSize size) {
        // Because PrioritizedMenuIcon keeps the IconSlot, and the PrioritizedMenuIconMap uses the MenuSize
        // every time it needs to derive the slot for a MenuIcon, all we need to do is update the MenuSize (& update MenuHolder)
        // The next time icons are placed, it will automatically re-calculate the slots for each icon
        // and then the priorities (which are based on the order of registration) will be used
        super.size = size.copy();

        // clear existing Inventory (since we must remake a new one)
        @Nullable final Inventory oldInv = this.getRawInventory();
        super.deleteInventory();

        // Re-open the inventory for players that were viewing it
        if (oldInv != null && !oldInv.getViewers().isEmpty()) {
            // We need to prevent the close event from triggering its close callbacks
            this.events.getIgnoreNextInventoryCloseEvent().set(true);
            // This will trigger the previous Inventory to close, hence the need to ignore the events
            openInternal(true);
        }
    }

    // ------------------------------------------------------------ //
    //                        Icon Management                       //
    // ------------------------------------------------------------ //
    @Override
    public @NotNull Map<String, MenuIcon> getMenuIcons() {
        return menuIcons.getMenuIcons();
    }

    @NotNull
    public T modifyIcons(@NotNull Consumer<IMenuIconsAccess> consumer) {
        consumer.accept(this.getMenuIconsAccess());
        return (T) this;
    }

    @Override
    public @NotNull IMenuIconsAccess getMenuIconsAccess() {
        return new MenuIconsAccess(this.getMenuSize(), this.menuIcons);
    }

    @Override
    public @Nullable MenuIcon getFillerIcon() {
        return this.menuIcons.get("filler").orElse(null);
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
     * @param needsUpdate An optional predicate to filter which icons need new builders.
     */
    private void placeIcons(@Nullable Predicate<MenuIcon> needsUpdate) {
        int tick = this.tickCounter.get();
        Map<String, Boolean> needsUpdateMap = new HashMap<>(); // Store <ID, Included>
        Map<String, ItemStack> itemStackMap = new HashMap<>(); // Store <ID, ItemStack>

        // Compile a list of all icons that need to be updated, based on the filter
        // Also generate the ItemStack for each icon, if it needs to be updated
        this.menuIcons.forEach((id, icon) -> {
            if (!icon.isEnabled()) { return; } // If not enabled, don't process it

            // Store if the icon needs to be updated
            if ((needsUpdate != null && !needsUpdate.test(icon))) {
                needsUpdateMap.put(id, false);
                return; // We return so that we don't put a stack in the stack map, we should leave the item alone
            }
            needsUpdateMap.put(id, true);
            // Generate the new ItemStack (one calculation) for this icon
            // We pass the last item, which is used for any stateful MenuIcon modifiers that rely on the previous item state
            @Nullable ItemStack item = icon.buildItem(tick, this.player);
            if (item != null) {
                itemStackMap.put(id, item);
            }
            // Store for state next update
            icon.setLastItem(item);
        });

        // Update the inventory slots, keep track of which slots we don't fill
        final MenuSize size = this.getMenuSize();
        Set<Integer> forFillerSlots = new HashSet<>();
        for (int i = 0; i < this.getSize(); i++) {
            @Nullable MenuIcon forHere = this.menuIcons.getActiveIconForSlot(size, i);

            // 1. If there is no icon for this slot, we need to fill it with the filler icon
            if (forHere == null) {
                forFillerSlots.add(i);
                continue;
            }

            // 2. If there is an icon here, and it does not need updating, we skip it
            if (!needsUpdateMap.getOrDefault(forHere.getId(), false)) {
                continue;
            }

            // 3. Retrieve the ItemStack for this icon, and place it in the slot
            @Nullable ItemStack item = itemStackMap.get(forHere.getId());
            super.setItem(i, item);
            // Note the behavior here. the Icon can return a null builder, which we will assume
            // was intended, and set the slot to null (making it an empty inventory slot)
            // We do not add the slot to the emptySlots set, because we don't want to fill it with the filler icon
        }

        // Place the filler icon in the slots that are needed
        placeFiller(needsUpdateMap, itemStackMap, forFillerSlots, tick);
    }

    protected void placeFiller(Map<String, Boolean> needsUpdateMap, Map<String, ItemStack> itemStackMap, Set<Integer> slots, int tick) {
        // Skip fill if the filler icon is disabled or not found
        @Nullable MenuIcon icon = this.menuIcons.getOrDefault("filler", null);
        if (icon == null || !icon.isEnabled()) { return; }

        // First, apply the excluded slots filter to the slots set
        slots.removeAll(this.options.getExcludedFillSlots());

        // Determine if we need to update this filler icon
        boolean needsUpdate = needsUpdateMap.getOrDefault("filler", false);
        @Nullable ItemStack fillerItem;
        if (needsUpdate) {
            // Pull from the item stack map (which already did the rebuild)
            fillerItem = itemStackMap.get("filler");
        }else {
            // Use the last item, since we don't want to update
            fillerItem = icon.getLastItem();
        }

        // Place the filler icon in the slots
        slots.forEach(slot -> super.setItem(slot, fillerItem));
    }










    // ------------------------------------------------------------ //
    //                        Builder Pattern                       //
    // ------------------------------------------------------------ //
    @SuppressWarnings("unchecked")
    public static sealed class Builder<T extends Builder<T>> permits PaginatedMenu.Builder {
        // Menu Details
        protected @NotNull MenuSize size;
        protected final @NotNull MenuTitleCalculator titleCalculator = new MenuTitleCalculator();
        // Menu Icons
        protected final PrioritizedMenuIconMap menuIcons = new PrioritizedMenuIconMap();
        // Additional Configuration
        protected final MenuEvents events;
        protected final MenuOptions options;

        Builder(@NotNull MenuSize size, @NotNull MenuEvents events, @NotNull MenuOptions options) {
            this.size = size;
            // Add the default filler icon
            this.menuIcons.add(MenuIcon.getDefaultFillerIcon().setId("filler"), null);
            // Set the initial events and options
            this.events = events;
            this.options = options;
        }

        public Builder(@NotNull MenuSize size) {
            this(size, new MenuEvents(), new MenuOptions());
        }
        public Builder(int rows) {
            this(new MenuSizeRows(rows));
        }
        public Builder(@NotNull InventoryType type) {
            this(new MenuSizeType(type));
        }

        public @NotNull MenuSize getSize() {
            return size;
        }

        @NotNull
        public T size(@NotNull MenuSize size) {
            Preconditions.checkNotNull(size, "Size must not be null.");
            this.size = size;
            return (T) this;
        }

        @NotNull
        public T title(@Nullable String title) {
            this.titleCalculator.setProvider((p) -> (title != null) ? title : " ");
            return (T) this;
        }
        @NotNull
        public T title(@NotNull MenuTitleProvider titleProvider) {
            Preconditions.checkNotNull(titleProvider, "Title callback must not be null.");
            this.titleCalculator.setProvider(titleProvider);
            return (T) this;
        }
        @NotNull
        public T titleReplacement(@NotNull CharSequence target, @NotNull CharSequence replacement) {
            Preconditions.checkNotNull(target, "Target must not be null.");
            Preconditions.checkNotNull(replacement, "Replacement must not be null.");
            this.titleCalculator.getReplacements().add(new MenuTitleReplacement(target, replacement));
            return (T) this;
        }

        @NotNull
        public T options(@NotNull MenuOptions.MenuOptionsModification modification) {
            Preconditions.checkNotNull(modification, "Modification must not be null.");
            modification.modify(this.options);
            return (T) this;
        }

        @NotNull
        public T events(@NotNull MenuEvents.MenuEventsModification modification) {
            Preconditions.checkNotNull(modification, "Modification must not be null.");
            modification.modify(this.events);
            return (T) this;
        }

        @NotNull
        public T fillerIcon(@Nullable MenuIcon fillerIcon) {
            if (fillerIcon == null) {
                this.menuIcons.remove("filler");
                return (T) this;
            }
            fillerIcon.setId("filler");
            this.menuIcons.add(fillerIcon, null);
            return (T) this;
        }

        @NotNull
        public T modifyIcons(@NotNull Consumer<IMenuIconsAccess> consumer) {
            consumer.accept(new MenuIconsAccess(this.size, this.menuIcons));
            return (T) this;
        }

        @NotNull
        @CheckReturnValue
        public SimpleMenu<?> build(@NotNull Player player) {
            Preconditions.checkNotNull(player, "Player must not be null.");
            return new SimpleMenu<>(this, player);
        }
    }
}
