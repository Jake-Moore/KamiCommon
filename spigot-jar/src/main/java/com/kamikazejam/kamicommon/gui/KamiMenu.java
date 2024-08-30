package com.kamikazejam.kamicommon.gui;

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
import com.kamikazejam.kamicommon.xseries.XMaterial;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    private final List<MenuOpenCallback> openCallbacks = new ArrayList<>();

    // Menu Options
    private final Set<UUID> ignoredClose = new HashSet<>(); // Set of Player UUID to ignore calling the close handler for
    private boolean allowItemPickup;

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
        this.placeItems();
        MenuTask.getAutoUpdateInventories().add(this);

        if (ignoreCloseHandler) {
            getIgnoredClose().add(player.getUniqueId());
        }

        InventoryView view = Objects.requireNonNull(player.openInventory(this.getInventory()));
        openCallbacks.forEach(callback -> callback.onOpen(player, view));
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

    public void whenOpened(@Nullable MenuOpenCallback menuOpen) {
        this.openCallbacks.add(menuOpen);
    }
    public void addOpenCallback(@Nullable MenuOpenCallback menuOpen) {
        this.openCallbacks.add(menuOpen);
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

    @Override
    public void clear() {
        super.clear();
        this.menuItems.clear();
    }

    protected void update(int tick) {
        this.placeItems((m) -> m.shouldUpdateForTick(tick));
    }
    public void placeItems() {
        this.placeItems(null);
    }

    public void placeItems(@Nullable Predicate<MenuItem> filter) {
        int size = this.getSize();
        for (MenuItem tickedItem : this.menuItems.values()) {
            if (filter != null && !filter.test(tickedItem)) { continue; }
            @Nullable ItemSlot itemSlot = tickedItem.getItemSlot();
            if (itemSlot == null) { continue; }

            // Build the new item, storing it back in the TickedItem for comparison on clicks
            ItemStack item = tickedItem.buildItem();
            if (item != null && item.getAmount() > 64) { item.setAmount(64); }
            tickedItem.setLastItem(item);

            // Update the inventory slots
            for (int slot : itemSlot.get(this)) {
                if (slot < 0 || slot >= size) { continue; }
                super.setItem(slot, item);
            }
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

    // ------------------------------------------------------------ //
    //                          Fill Methods                        //
    // ------------------------------------------------------------ //

    @NotNull
    public ItemStack getDefaultFiller() {
        XMaterial mat = XMaterial.GRAY_STAINED_GLASS_PANE;
        return new ItemBuilder(mat, 1, mat.getData()).setName(" ").toItemStack();
    }

    @NotNull
    public KamiMenu fill() {
        this.fill(getDefaultFiller());
        return this;
    }

    @NotNull
    public KamiMenu fill(@NotNull Integer... ignoreSlots) {
        this.fill(getDefaultFiller(), Arrays.asList(ignoreSlots));
        return this;
    }

    @NotNull
    public KamiMenu fill(@NotNull List<Integer> ignoreSlots) {
        this.fill(getDefaultFiller(), ignoreSlots);
        return this;
    }

    @NotNull
    public KamiMenu fill(@NotNull IBuilder filler) {
        return fill(filler.toItemStack());
    }

    @NotNull
    public KamiMenu fill(@NotNull IBuilder filler, @NotNull Integer... ignoreSlots) {
        return fill(filler.toItemStack(), Arrays.asList(ignoreSlots));
    }

    @NotNull
    public KamiMenu fill(@NotNull IBuilder filler, @NotNull List<Integer> ignoreSlots) {
        return fill(filler.toItemStack(), ignoreSlots);
    }

    @NotNull
    public KamiMenu fill(@NotNull ItemStack filler) {
        return fill(filler, List.of());
    }

    @NotNull
    public KamiMenu fill(@NotNull ItemStack filler, @NotNull Integer... ignoreSlots) {
        return fill(filler, Arrays.asList(ignoreSlots));
    }

    @NotNull
    public KamiMenu fill(@NotNull ItemStack filler, @NotNull List<Integer> ignoreSlots) {
        int empty = getInventory().firstEmpty();
        while (empty != -1) {
            if (!ignoreSlots.contains(empty)) {
                this.setItem(empty, filler);
            }
            empty = getInventory().firstEmpty();
        }
        return this;
    }

    @NotNull
    public KamiMenu fill(@NotNull MenuItem menuItem) {
        if (!menuItem.isEnabled()) { return this; }
        int empty = getInventory().firstEmpty();
        while (empty != -1) {
            // Copy the item, editing the slot to be the fill slot
            this.addMenuItem(new MenuItem(true, new StaticItemSlot(empty), menuItem.getIBuilders()));
            empty = getInventory().firstEmpty();
        }
        return this;
    }
}
