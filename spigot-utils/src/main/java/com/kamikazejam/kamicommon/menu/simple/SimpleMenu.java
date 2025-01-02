package com.kamikazejam.kamicommon.menu.simple;

import com.kamikazejam.kamicommon.SpigotUtilsSource;
import com.kamikazejam.kamicommon.menu.Menu;
import com.kamikazejam.kamicommon.menu.api.MenuHolder;
import com.kamikazejam.kamicommon.menu.api.callbacks.MenuTitleCallback;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.access.IMenuIconsAccess;
import com.kamikazejam.kamicommon.menu.api.icons.access.MenuIconsAccess;
import com.kamikazejam.kamicommon.menu.api.icons.interfaces.UpdatingMenu;
import com.kamikazejam.kamicommon.menu.api.struct.MenuEvents;
import com.kamikazejam.kamicommon.menu.api.struct.MenuOptions;
import com.kamikazejam.kamicommon.menu.api.struct.SlotData;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSizeRows;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSizeType;
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
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private final Map<Integer, SlotData> menuSlots = new ConcurrentHashMap<>();
    private final MenuEvents events;
    private final MenuOptions options;
    // Internal Data
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private final AtomicInteger tickCounter = new AtomicInteger(0);

    // Constructor (Deep Copying from Builder)
    private SimpleMenu(@NotNull Builder builder, @NotNull Player player) {
        super(builder.size.copy(), Optional.ofNullable(builder.titleCallback).map(t -> t.getTitle(player)).orElse(" "));
        this.player = player;
        builder.menuIcons.forEach((id, icon) -> this.menuIcons.put(id, icon.copy()));
        builder.menuSlots.forEach((slot, data) -> this.menuSlots.put(slot, data.copy()));
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
        if (!PlayerUtil.isFullyValidPlayer(player)) { return null; }

        // Reset the tick counter for icons that auto update if necessary
        if (options.isResetVisualsOnOpen()) {
            tickCounter.set(0);
        }

        // Place all icons into the inventory
        // This method will also handle the filler icon placement
        this.placeIcons(null);

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

    public void resizeMenu(@NotNull MenuSize newSize) {

    }

    // ------------------------------------------------------------ //
    //                        Icon Management                       //
    // ------------------------------------------------------------ //

    @NotNull
    public SimpleMenu modifyIcons(@NotNull Consumer<IMenuIconsAccess> consumer) {
        consumer.accept(this.getMenuIconsAccess());
        return this;
    }

    @Override
    public @NotNull IMenuIconsAccess getMenuIconsAccess() {
        return new MenuIconsAccess(this.getMenuSize(), this.menuIcons, this.menuSlots);
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
            @Nullable ItemStack item = icon.buildItem(tick > 0 && icon.isCycleBuilderForTick(tick), icon.getLastItem(), this.player);
            if (item != null) {
                itemStackMap.put(id, item);
            }
            // Store for state next update
            icon.setLastItem(item);
        });

        // Update the inventory slots, keep track of which slots we don't fill
        Set<Integer> forFillerSlots = new HashSet<>();
        for (int i = 0; i < this.getSize(); i++) {
            // Fetch our icon for this slot, skipping the slot if we don't have an icon or it failed the filter
            @Nullable String iconID = Optional.ofNullable(this.menuSlots.get(i)).map(SlotData::getId).orElse(null);

            // 1. If there is no icon for this slot, we need to fill it with the filler icon
            if (iconID == null) {
                forFillerSlots.add(i);
                continue;
            }

            // 2. If there is an icon here, and it does not need updating, we skip it
            if (!needsUpdateMap.getOrDefault(iconID, false)) {
                continue;
            }

            // 3. Retrieve the ItemStack for this icon, and place it in the slot
            @Nullable ItemStack item = itemStackMap.get(iconID);
            super.setItem(i, item);
            // Note the behavior here. the Icon can return a null builder, which we will assume
            // was intended, and set the slot to null (making it an empty inventory slot)
            // We do not add the slot to the emptySlots set, because we don't want to fill it with the filler icon
        }

        // Place the filler icon in the slots that are needed
        placeFiller(needsUpdateMap, itemStackMap, forFillerSlots, tick);
    }

    private void placeFiller(Map<String, Boolean> needsUpdateMap, Map<String, ItemStack> itemStackMap, Set<Integer> slots, int tick) {
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
    public static final class Builder {
        // Menu Details
        private @NotNull MenuSize size;
        private @Nullable MenuTitleCallback titleCallback;
        // Menu Icons
        private final Map<String, MenuIcon> menuIcons = new ConcurrentHashMap<>();
        @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
        private final Map<Integer, SlotData> menuSlots = new ConcurrentHashMap<>();
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
            this.titleCallback = (p) -> (title != null) ? title : " ";
            return this;
        }
        @NotNull
        public Builder title(@NotNull MenuTitleCallback titleCallback) {
            Preconditions.checkNotNull(titleCallback, "Title callback must not be null.");
            this.titleCallback = titleCallback;
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
            consumer.accept(new MenuIconsAccess(this.size, this.menuIcons, this.menuSlots));
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
