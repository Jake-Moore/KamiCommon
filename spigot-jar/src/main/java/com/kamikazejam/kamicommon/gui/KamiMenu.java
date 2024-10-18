package com.kamikazejam.kamicommon.gui;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.gui.clicks.MenuClick;
import com.kamikazejam.kamicommon.gui.clicks.MenuClickEvent;
import com.kamikazejam.kamicommon.gui.clicks.MenuClickPage;
import com.kamikazejam.kamicommon.gui.clicks.PlayerSlotClick;
import com.kamikazejam.kamicommon.gui.items.MenuItem;
import com.kamikazejam.kamicommon.gui.items.interfaces.IBuilderModifier;
import com.kamikazejam.kamicommon.gui.items.slots.ItemSlot;
import com.kamikazejam.kamicommon.gui.items.slots.StaticItemSlot;
import com.kamikazejam.kamicommon.gui.loader.MenuItemLoader;
import com.kamikazejam.kamicommon.gui.page.PagedKamiMenu;
import com.kamikazejam.kamicommon.gui.struct.MenuSize;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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

@Getter @Setter
@Accessors(chain = true)
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class KamiMenu extends MenuHolder {
    public interface MenuOpenCallback {
        void onOpen(@NotNull Player player, @NotNull InventoryView view);
    }

    // MetaData (for various data storage)
    private final Map<String, Object> metaData = new ConcurrentHashMap<>();

    // Menu Items (maps id to item with that id)
    private final Map<String, MenuItem> menuItems = new ConcurrentHashMap<>();

    // Player Menu Clicks
    private final List<PlayerSlotClick> playerInvClicks = new ArrayList<>();
    private final Map<Integer, List<PlayerSlotClick>> playerSlotClicks = new ConcurrentHashMap<>();

    // Menu Callbacks
    private final List<Predicate<InventoryClickEvent>> clickPredicates = new ArrayList<>();
    private final List<Consumer<InventoryCloseEvent>> closeConsumers = new ArrayList<>();
    private final Map<String, MenuOpenCallback> openCallbacks = new ConcurrentHashMap<>(); // Map<Id, Callback>

    // Menu Options
    private final Set<UUID> ignoredClose = new HashSet<>(); // Set of Player UUID to ignore calling the close handler for
    private boolean allowItemPickup;
    // Filler is a MenuItem so that it can have the same features as other items (rotating materials, clicks, etc)
    private @Nullable MenuItem fillerItem = new MenuItem(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setName(" "), -1).setId("filler");
    private final Set<Integer> excludedFillSlots = new HashSet<>();
    @ApiStatus.Internal
    private final AtomicInteger tickCounter = new AtomicInteger(0);
    
    private boolean cancelOnClick = true;

    public KamiMenu(@NotNull String name, int rows) {
        super(name, rows);
    }
    public KamiMenu(@NotNull String name, @NotNull InventoryType type) {
        super(name, type);
    }
    public KamiMenu(@NotNull String name, @NotNull MenuSize size) {
        super(name, size);
    }

    @NotNull
    public PagedKamiMenu wrapAsPaged() {
        return new PagedKamiMenu(this);
    }

    @NotNull
    public InventoryView openMenu(@NotNull Player player) {
        return openMenu(player, false);
    }

    @NotNull
    public InventoryView openMenu(@NotNull Player player, boolean ignoreCloseHandler) {
        // Place all items into the inventory
        this.placeItems(null);
        MenuTask.getAutoUpdateInventories().add(this);

        if (ignoreCloseHandler) {
            getIgnoredClose().add(player.getUniqueId());
        }

        InventoryView view = Objects.requireNonNull(player.openInventory(this.getInventory()));
        openCallbacks.values().forEach(callback -> callback.onOpen(player, view));
        return view;
    }

    public void closeInventory(@NotNull Player player) {
        closeInventory(player, false);
    }

    public void closeInventory(@NotNull Player player, boolean ignoreCloseHandler) {
        if (ignoreCloseHandler) {
            getIgnoredClose().add(player.getUniqueId());
        }

        player.closeInventory();
    }

    public void closeAll() {
        @Nullable Inventory inv = this.inventory;
        if (inv == null) { return; }
        inv.getViewers().forEach(HumanEntity::closeInventory);
    }

    /**
     * @return The id this callback was registered under. For direct removal from the callback map.
     */
    public @NotNull String whenOpened(@Nullable MenuOpenCallback menuOpen) {
        return this.addOpenCallback(menuOpen);
    }
    /**
     * @return The id this callback was registered under. For direct removal from the callback map.
     */
    public @NotNull String addOpenCallback(@Nullable MenuOpenCallback menuOpen) {
        String id = UUID.randomUUID().toString();
        this.openCallbacks.put(id, menuOpen);
        return id;
    }
    public void setOpenCallback(@NotNull String id, @Nullable MenuOpenCallback menuOpen) {
        this.openCallbacks.put(id, menuOpen);
    }

    public void addIgnoredClose(@NotNull Player player) {
        this.ignoredClose.add(player.getUniqueId());
    }
    public void removeIgnoredClose(@NotNull Player player) {
        this.ignoredClose.remove(player.getUniqueId());
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

    protected void update() {
        int tick = this.tickCounter.get();
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
    public KamiMenu setMenuClick(@NotNull String id, @NotNull MenuClick click) {
        this.getMenuItem(id).ifPresent(item -> item.setMenuClick(click));
        return this;
    }
    @NotNull
    public KamiMenu setMenuClick(@NotNull String id, @NotNull MenuClickPage click) {
        this.getMenuItem(id).ifPresent(item -> item.setMenuClick(click));
        return this;
    }
    @NotNull
    public KamiMenu setMenuClick(@NotNull String id, @NotNull MenuClickEvent click) {
        this.getMenuItem(id).ifPresent(item -> item.setMenuClick(click));
        return this;
    }
    @NotNull
    public KamiMenu setModifier(@NotNull String id, @NotNull IBuilderModifier modifier) {
        this.getMenuItem(id).ifPresent(item -> item.setModifier(modifier));
        return this;
    }
    @NotNull
    public KamiMenu setAutoUpdate(@NotNull String id, @NotNull IBuilderModifier modifier, int tickInterval) {
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
    //              Callbacks, Consumers, and Predicates            //
    // ------------------------------------------------------------ //

    /**
     * Listen to a player inventory click at a specific slot.
     * @param slot The player inventory slot to listen to.
     */
    @NotNull
    public KamiMenu onPlayerSlotClick(int slot, @NotNull PlayerSlotClick click) {
        this.playerSlotClicks.computeIfAbsent(slot, k -> new ArrayList<>()).add(click);
        return this;
    }

    /**
     * Listen to all player inventory clicks.
     * @param click The callback to run when a player clicks a slot in their inventory.
     */
    @NotNull
    public KamiMenu onPlayerSlotClick(@NotNull PlayerSlotClick click) {
        this.playerInvClicks.add(click);
        return this;
    }

    /**
     * Add a predicate on InventoryClickEvent that must pass for click handlers to be called.
     */
    @NotNull
    public KamiMenu addClickPredicate(@NotNull Predicate<InventoryClickEvent> predicate) {
        this.clickPredicates.add(predicate);
        return this;
    }

    /**
     * Add a consumer that runs when the inventory is closed, with access to {@link InventoryCloseEvent}.
     */
    @NotNull
    public KamiMenu addCloseConsumer(@NotNull Consumer<InventoryCloseEvent> consumer) {
        this.closeConsumers.add(consumer);
        return this;
    }



    private KamiMenu fill() {
        if (this.fillerItem == null || !this.fillerItem.isEnabled()) { return this; }

        for (int i = 0; i < getInventory().getSize(); i++) {
            if (excludedFillSlots.contains(i)) { continue; }
            ItemStack here = getInventory().getItem(i);
            if (here == null || here.getType() == Material.AIR) {
                MenuItem item = fillerItem.copy().setItemSlot(new StaticItemSlot(i));
                this.addMenuItem(item); // Cache so it gets updated like other items
                this.placeItem(null, item); // Set the item in the inventory
            }
        }

        return this;
    }

    public void setFillerItem(@Nullable MenuItem fillerItem) {
        this.fillerItem = fillerItem;
        if (fillerItem != null) {
            fillerItem.setId("filler");
        }
    }
}
