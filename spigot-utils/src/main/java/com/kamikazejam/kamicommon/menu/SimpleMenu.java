package com.kamikazejam.kamicommon.menu;

import com.kamikazejam.kamicommon.SpigotUtilsSource;
import com.kamikazejam.kamicommon.menu.items.MenuItem;
import com.kamikazejam.kamicommon.menu.items.access.IMenuItemsAccess;
import com.kamikazejam.kamicommon.menu.items.access.MenuItemsAccess;
import com.kamikazejam.kamicommon.menu.items.slots.ItemSlot;
import com.kamikazejam.kamicommon.menu.items.slots.StaticItemSlot;
import com.kamikazejam.kamicommon.menu.struct.MenuEvents;
import com.kamikazejam.kamicommon.menu.struct.MenuHolder;
import com.kamikazejam.kamicommon.menu.struct.MenuOptions;
import com.kamikazejam.kamicommon.menu.struct.size.MenuSize;
import com.kamikazejam.kamicommon.menu.struct.size.MenuSizeRows;
import com.kamikazejam.kamicommon.menu.struct.size.MenuSizeType;
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
public class SimpleMenu extends MenuHolder implements Menu, UpdatingMenu {
    // Fields
    private final Player player;
    private final Map<String, MenuItem> menuItems = new ConcurrentHashMap<>();
    private final MenuEvents events;
    private final MenuOptions options;
    // Internal Data
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private final AtomicInteger tickCounter = new AtomicInteger(0);

    // Constructor (Deep Copying from Builder)
    private SimpleMenu(@NotNull Builder builder, @NotNull Player player) {
        super(builder.size.copy(), builder.title);
        this.player = player;
        builder.menuItems.forEach((id, item) -> this.menuItems.put(id, item.copy()));
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

        // Reset the tick counter for items that auto update if necessary
        if (options.isResetVisualsOnOpen()) {
            tickCounter.set(0);
        }

        // Place all items into the inventory
        this.placeItems(null);
        // After the initial placement of icons, run a fill
        // any AIR that's left after normal items gets assumed by the filler ItemSlot
        this.fill();

        // Register this menu for auto-updating
        SpigotUtilsSource.getMenuManager().autoUpdateInventories.add(this);

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
    //                        Item Management                       //
    // ------------------------------------------------------------ //

    @NotNull
    public SimpleMenu modifyItems(@NotNull Consumer<IMenuItemsAccess> consumer) {
        consumer.accept(new MenuItemsAccess(this.menuItems));
        return this;
    }

    // ------------------------------------------------------------ //
    //                   Item Update Management                     //
    // ------------------------------------------------------------ //
    @ApiStatus.Internal
    @Override
    public void updateOneTick() {
        // getAndIncrement means we start at 1, since 0th tick should have been the call to open()
        int tick = this.tickCounter.incrementAndGet();
        this.placeItems((m) -> m.needsModification(tick));
    }

    private void placeItems(@Nullable Predicate<MenuItem> filter) {
        // After filler items have possibly been added to the menuItems, we should place all items
        this.menuItems.values().forEach(item -> this.placeItem(filter, item));
    }

    private void placeItem(@Nullable Predicate<MenuItem> filter, @NotNull MenuItem menuItem) {
        if (!menuItem.isEnabled() || (filter != null && !filter.test(menuItem))) { return; }
        int tick = this.tickCounter.get();

        @Nullable ItemSlot itemSlot = menuItem.getItemSlot();
        if (itemSlot == null) { return; }

        // The tick determines if we should cycle the builder
        // We also skip tick 0 since the modulo operation will always be true
        @Nullable ItemStack item = menuItem.buildItem(tick > 0 && menuItem.isCycleBuilderForTick(tick));
        this.placeItemStack(item, menuItem, itemSlot);
    }

    public void updateItem(@NotNull String id) {
        this.modifyItems((access) -> access.getMenuItem(id).ifPresent(this::updateItem));
    }

    public void updateItem(@NotNull MenuItem menuItem) {
        this.updateItem(null, menuItem);
    }

    public void updateItem(@Nullable Predicate<MenuItem> filter, @NotNull MenuItem menuItem) {
        if (!menuItem.isEnabled() || (filter != null && !filter.test(menuItem))) { return; }
        @Nullable ItemSlot itemSlot = menuItem.getItemSlot();
        if (itemSlot == null) { return; }

        // Build the new item, storing it back in the MenuItem for comparison on clicks
        @Nullable ItemStack item = menuItem.buildItem(false);
        this.placeItemStack(item, menuItem, itemSlot);
    }

    private void placeItemStack(@Nullable ItemStack item, @NotNull MenuItem menuItem, @NotNull ItemSlot itemSlot) {
        if (item != null && item.getAmount() > 64) { item.setAmount(64); }
        // Store the item back in the MenuItem for comparison on clicks
        menuItem.setLastItem(item);

        // Update the inventory slots
        int size = this.getSize();
        for (int slot : itemSlot.get(this)) {
            if (slot < 0 || slot >= size) { continue; }
            super.setItem(slot, item);
        }
    }

    private void fill() {
        @Nullable MenuItem fillerItem = this.menuItems.getOrDefault("filler", null);
        if (fillerItem == null || !fillerItem.isEnabled()) { return; }

        // Find the slots that need to be filled
        List<Integer> slotsToFill = new ArrayList<>();
        for (int i = 0; i < getInventory().getSize(); i++) {
            if (this.options.getExcludedFillSlots().contains(i)) { continue; }
            ItemStack here = getInventory().getItem(i);
            if (here != null && here.getType() != Material.AIR) { continue; }

            slotsToFill.add(i);
        }
        // Update the filler's ItemSlot value
        if (fillerItem.getItemSlot() instanceof StaticItemSlot staticSlots) {
            // This will occur if we reopen the menu, and when we do we need to re-evaluate the slots
            List<Integer> newSlots = new ArrayList<>(slotsToFill);
            newSlots.addAll(staticSlots.getSlots());
            fillerItem.setItemSlot(new StaticItemSlot(newSlots));
        }else {
            fillerItem.setItemSlot(new StaticItemSlot(slotsToFill));
        }

        this.menuItems.put("filler", fillerItem);

        // Make sure the filler item is updated in the Menu
        this.placeItem(null, fillerItem); // Set the item in the inventory
    }










    public static final class Builder {
        // Menu Details
        private @NotNull MenuSize size;
        private @Nullable String title;
        // Menu Items
        private final Map<String, MenuItem> menuItems = new ConcurrentHashMap<>();
        // Additional Configuration
        private final MenuEvents events = new MenuEvents();
        private final MenuOptions options = new MenuOptions();

        public Builder(@NotNull MenuSize size) {
            Preconditions.checkNotNull(size, "Size must not be null.");
            this.size = size;
            // Add the default filler item
            this.menuItems.put("filler", MenuItem.getDefaultFillerItem());

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
        public Builder fillerItem(@Nullable MenuItem fillerItem) {
            if (fillerItem == null) {
                this.menuItems.remove("filler");
                return this;
            }
            fillerItem.setId("filler");
            this.menuItems.put("filler", fillerItem);
            return this;
        }

        @NotNull
        public Builder modifyItems(@NotNull Consumer<IMenuItemsAccess> consumer) {
            consumer.accept(new MenuItemsAccess(this.menuItems));
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
