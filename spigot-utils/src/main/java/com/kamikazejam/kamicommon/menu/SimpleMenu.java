package com.kamikazejam.kamicommon.menu;

import com.kamikazejam.kamicommon.SpigotUtilsSource;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.menu.clicks.MenuClick;
import com.kamikazejam.kamicommon.menu.clicks.MenuClickEvent;
import com.kamikazejam.kamicommon.menu.clicks.MenuClickPage;
import com.kamikazejam.kamicommon.menu.items.MenuItem;
import com.kamikazejam.kamicommon.menu.items.interfaces.IBuilderModifier;
import com.kamikazejam.kamicommon.menu.items.slots.ItemSlot;
import com.kamikazejam.kamicommon.menu.items.slots.StaticItemSlot;
import com.kamikazejam.kamicommon.menu.loaders.MenuItemLoader;
import com.kamikazejam.kamicommon.menu.struct.MenuEvents;
import com.kamikazejam.kamicommon.menu.struct.MenuHolder;
import com.kamikazejam.kamicommon.menu.struct.MenuOptions;
import com.kamikazejam.kamicommon.menu.struct.size.MenuSize;
import com.kamikazejam.kamicommon.menu.struct.size.MenuSizeRows;
import com.kamikazejam.kamicommon.menu.struct.size.MenuSizeType;
import com.kamikazejam.kamicommon.util.PlayerUtil;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
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

        // Place all items into the inventory
        this.placeItems(null);
        SpigotUtilsSource.getMenuManager().autoUpdateInventories.add(this);

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

    @CheckReturnValue
    public @NotNull MenuItem addMenuItem(@NotNull IBuilder builder, int slot) {
        return this.addMenuItem(new MenuItem(true, new StaticItemSlot(slot), builder));
    }
    @CheckReturnValue
    public @NotNull MenuItem addMenuItem(@NotNull IBuilder builder, @NotNull ItemSlot slot) {
        return this.addMenuItem(new MenuItem(true, slot, builder));
    }
    @CheckReturnValue
    public @NotNull MenuItem addMenuItem(@NotNull ItemStack stack, int slot) {
        return this.addMenuItem(new MenuItem(true, new StaticItemSlot(slot), new ItemBuilder(stack)));
    }
    @CheckReturnValue
    public @NotNull MenuItem addMenuItem(@NotNull ItemStack stack, @NotNull ItemSlot slot) {
        return this.addMenuItem(new MenuItem(true, slot, new ItemBuilder(stack)));
    }

    @CheckReturnValue
    public @NotNull MenuItem addMenuItem(@NotNull String id, @NotNull IBuilder builder, int slot) {
        return this.addMenuItem(new MenuItem(true, new StaticItemSlot(slot), builder).setId(id));
    }
    @CheckReturnValue
    public @NotNull MenuItem addMenuItem(@NotNull String id, @NotNull ItemStack stack, int slot) {
        return this.addMenuItem(new MenuItem(true, new StaticItemSlot(slot), new ItemBuilder(stack)).setId(id));
    }
    @CheckReturnValue
    public @NotNull MenuItem addMenuItem(@NotNull String id, @NotNull IBuilder builder, @NotNull ItemSlot slot) {
        return this.addMenuItem(new MenuItem(true, slot, builder).setId(id));
    }
    @CheckReturnValue
    public @NotNull MenuItem addMenuItem(@NotNull String id, @NotNull ItemStack stack, @NotNull ItemSlot slot) {
        return this.addMenuItem(new MenuItem(true, slot, new ItemBuilder(stack)).setId(id));
    }

    @CheckReturnValue
    public @NotNull MenuItem addMenuItem(@NotNull ConfigurationSection section, @NotNull String key, @Nullable Player player) {
        return this.addMenuItem(MenuItemLoader.load(section.getConfigurationSection(key), player));
    }
    @CheckReturnValue
    public @NotNull MenuItem addMenuItem(@NotNull ConfigurationSection section, @NotNull String key) {
        return this.addMenuItem(MenuItemLoader.load(section.getConfigurationSection(key)));
    }
    @CheckReturnValue
    public @NotNull MenuItem addMenuItem(@NotNull ConfigurationSection section, @Nullable Player player) {
        return this.addMenuItem(MenuItemLoader.load(section, player));
    }
    @CheckReturnValue
    public @NotNull MenuItem addMenuItem(@NotNull ConfigurationSection section) {
        return this.addMenuItem(MenuItemLoader.load(section));
    }

    public @NotNull MenuItem addMenuItem(@NotNull MenuItem menuItem) {
        this.menuItems.put(menuItem.getId(), menuItem);
        return menuItem;
    }

    public void removeMenuItem(@NotNull String id) {
        MenuItem item = this.menuItems.remove(id);
        if (item != null) {
            // We removed a MenuItem -> remove the item from the inventory
            item.getSlots(this).forEach(slot -> this.setItem(slot, (ItemStack) null));
        }
    }

    @Override
    public void clear() {
        super.clear();
        this.menuItems.clear();
    }

    // ------------------------------------------------------------ //
    //                   Item Management (by ID)                    //
    // ------------------------------------------------------------ //
    /**
     * Retrieve a menu item by its id
     */
    @NotNull
    public Optional<MenuItem> getMenuItem(@NotNull String id) {
        if (!menuItems.containsKey(id)) { return Optional.empty(); }
        return Optional.ofNullable(menuItems.get(id));
    }

    @NotNull
    public SimpleMenu setMenuClick(@NotNull String id, @NotNull MenuClick click) {
        this.getMenuItem(id).ifPresent(item -> item.setMenuClick(click));
        return this;
    }
    @NotNull
    public SimpleMenu setMenuClick(@NotNull String id, @NotNull MenuClickPage click) {
        this.getMenuItem(id).ifPresent(item -> item.setMenuClick(click));
        return this;
    }
    @NotNull
    public SimpleMenu setMenuClick(@NotNull String id, @NotNull MenuClickEvent click) {
        this.getMenuItem(id).ifPresent(item -> item.setMenuClick(click));
        return this;
    }
    @NotNull
    public SimpleMenu setModifier(@NotNull String id, @NotNull IBuilderModifier modifier) {
        this.getMenuItem(id).ifPresent(item -> item.setModifier(modifier));
        return this;
    }
    @NotNull
    public SimpleMenu setAutoUpdate(@NotNull String id, @NotNull IBuilderModifier modifier, int tickInterval) {
        this.getMenuItem(id).ifPresent(item -> item.setAutoUpdate(modifier, tickInterval));
        return this;
    }
    public boolean isValidMenuItemID(@NotNull String id) {
        return menuItems.containsKey(id);
    }
    @NotNull
    public Set<String> getMenuItemIDs() {
        return menuItems.keySet();
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
        this.menuItems.values().forEach(item -> this.placeItem(filter, item));
        // Automatically fill using filler item, which can be set to null to disable
        this.fill();
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
        this.getMenuItem(id).ifPresent(this::updateItem);
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

    private SimpleMenu fill() {
        @Nullable MenuItem fillerItem = this.options.getFillerItem();
        if (fillerItem == null || !fillerItem.isEnabled()) { return this; }

        for (int i = 0; i < getInventory().getSize(); i++) {
            if (this.options.getExcludedFillSlots().contains(i)) { continue; }
            ItemStack here = getInventory().getItem(i);
            if (here == null || here.getType() == Material.AIR) {
                MenuItem item = fillerItem.copy().setItemSlot(new StaticItemSlot(i));
                this.addMenuItem(item); // Cache so it gets updated like other items
                this.placeItem(null, item); // Set the item in the inventory
            }
        }

        return this;
    }












    @Getter @Setter
    @Accessors(fluent = true, chain = true)
    public static final class Builder {
        // Menu Details
        @Setter(AccessLevel.NONE)
        private @NotNull MenuSize size;
        private @Nullable String title;
        // Menu Items
        @Getter(AccessLevel.NONE)
        private final Map<String, MenuItem> menuItems = new ConcurrentHashMap<>();
        // Additional Configuration
        private final MenuEvents events = new MenuEvents();
        private final MenuOptions options = new MenuOptions();

        public Builder(@NotNull MenuSize size) {
            Preconditions.checkNotNull(size, "Size must not be null.");
            this.size = size;
        }
        public Builder(int rows) {
            this(new MenuSizeRows(rows));
        }
        public Builder(@NotNull InventoryType type) {
            this(new MenuSizeType(type));
        }

        public void size(@NotNull MenuSize size) {
            Preconditions.checkNotNull(size, "Size must not be null.");
            this.size = size;
        }

        // ------------------------------------------------------------ //
        //                        Item Management                       //
        // ------------------------------------------------------------ //

        @CheckReturnValue
        public @NotNull MenuItem addMenuItem(@NotNull IBuilder builder, int slot) {
            return this.addMenuItem(new MenuItem(true, new StaticItemSlot(slot), builder));
        }
        @CheckReturnValue
        public @NotNull MenuItem addMenuItem(@NotNull IBuilder builder, @NotNull ItemSlot slot) {
            return this.addMenuItem(new MenuItem(true, slot, builder));
        }
        @CheckReturnValue
        public @NotNull MenuItem addMenuItem(@NotNull ItemStack stack, int slot) {
            return this.addMenuItem(new MenuItem(true, new StaticItemSlot(slot), new ItemBuilder(stack)));
        }
        @CheckReturnValue
        public @NotNull MenuItem addMenuItem(@NotNull ItemStack stack, @NotNull ItemSlot slot) {
            return this.addMenuItem(new MenuItem(true, slot, new ItemBuilder(stack)));
        }

        @CheckReturnValue
        public @NotNull MenuItem addMenuItem(@NotNull String id, @NotNull IBuilder builder, int slot) {
            return this.addMenuItem(new MenuItem(true, new StaticItemSlot(slot), builder).setId(id));
        }
        @CheckReturnValue
        public @NotNull MenuItem addMenuItem(@NotNull String id, @NotNull ItemStack stack, int slot) {
            return this.addMenuItem(new MenuItem(true, new StaticItemSlot(slot), new ItemBuilder(stack)).setId(id));
        }
        @CheckReturnValue
        public @NotNull MenuItem addMenuItem(@NotNull String id, @NotNull IBuilder builder, @NotNull ItemSlot slot) {
            return this.addMenuItem(new MenuItem(true, slot, builder).setId(id));
        }
        @CheckReturnValue
        public @NotNull MenuItem addMenuItem(@NotNull String id, @NotNull ItemStack stack, @NotNull ItemSlot slot) {
            return this.addMenuItem(new MenuItem(true, slot, new ItemBuilder(stack)).setId(id));
        }

        @CheckReturnValue
        public @NotNull MenuItem addMenuItem(@NotNull ConfigurationSection section, @NotNull String key, @Nullable Player player) {
            return this.addMenuItem(MenuItemLoader.load(section.getConfigurationSection(key), player));
        }
        @CheckReturnValue
        public @NotNull MenuItem addMenuItem(@NotNull ConfigurationSection section, @NotNull String key) {
            return this.addMenuItem(MenuItemLoader.load(section.getConfigurationSection(key)));
        }
        @CheckReturnValue
        public @NotNull MenuItem addMenuItem(@NotNull ConfigurationSection section, @Nullable Player player) {
            return this.addMenuItem(MenuItemLoader.load(section, player));
        }
        @CheckReturnValue
        public @NotNull MenuItem addMenuItem(@NotNull ConfigurationSection section) {
            return this.addMenuItem(MenuItemLoader.load(section));
        }

        public @NotNull MenuItem addMenuItem(@NotNull MenuItem menuItem) {
            this.menuItems.put(menuItem.getId(), menuItem);
            return menuItem;
        }

        public @Nullable MenuItem removeMenuItem(@NotNull String id) {
            return this.menuItems.remove(id);
        }

        public void clearMenuItems() {
            this.menuItems.clear();
        }


        // ------------------------------------------------------------ //
        //                   Item Management (by ID)                    //
        // ------------------------------------------------------------ //
        /**
         * Retrieve a menu item by its id
         */
        @NotNull
        public Optional<MenuItem> getMenuItem(@NotNull String id) {
            if (!menuItems.containsKey(id)) { return Optional.empty(); }
            return Optional.ofNullable(menuItems.get(id));
        }

        @NotNull
        public Builder setMenuClick(@NotNull String id, @NotNull MenuClick click) {
            this.getMenuItem(id).ifPresent(item -> item.setMenuClick(click));
            return this;
        }
        @NotNull
        public Builder setMenuClick(@NotNull String id, @NotNull MenuClickPage click) {
            this.getMenuItem(id).ifPresent(item -> item.setMenuClick(click));
            return this;
        }
        @NotNull
        public Builder setMenuClick(@NotNull String id, @NotNull MenuClickEvent click) {
            this.getMenuItem(id).ifPresent(item -> item.setMenuClick(click));
            return this;
        }
        @NotNull
        public Builder setModifier(@NotNull String id, @NotNull IBuilderModifier modifier) {
            this.getMenuItem(id).ifPresent(item -> item.setModifier(modifier));
            return this;
        }
        @NotNull
        public Builder setAutoUpdate(@NotNull String id, @NotNull IBuilderModifier modifier, int tickInterval) {
            this.getMenuItem(id).ifPresent(item -> item.setAutoUpdate(modifier, tickInterval));
            return this;
        }
        public boolean isValidMenuItemID(@NotNull String id) {
            return menuItems.containsKey(id);
        }
        @NotNull
        public Set<String> getMenuItemIDs() {
            return menuItems.keySet();
        }


        @NotNull
        @CheckReturnValue
        public SimpleMenu build(@NotNull Player player) {
            Preconditions.checkNotNull(player, "Player must not be null.");
            return new SimpleMenu(this, player);
        }
    }
}
