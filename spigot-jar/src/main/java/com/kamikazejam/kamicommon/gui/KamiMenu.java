package com.kamikazejam.kamicommon.gui;

import com.kamikazejam.kamicommon.gui.clicks.PlayerSlotClick;
import com.kamikazejam.kamicommon.gui.items.MenuItem;
import com.kamikazejam.kamicommon.gui.items.interfaces.IMenuItem;
import com.kamikazejam.kamicommon.gui.items.slots.ItemSlot;
import com.kamikazejam.kamicommon.gui.items.slots.StaticItemSlot;
import com.kamikazejam.kamicommon.gui.loader.MenuItemLoader;
import com.kamikazejam.kamicommon.gui.page.PageBuilder;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.xseries.XMaterial;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import io.netty.util.internal.ConcurrentSet;
import lombok.AccessLevel;
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

    // Menu Items
    private final @Nullable PageBuilder parent;
    private final Set<MenuItem> menuItems = new ConcurrentSet<>();

    // Player Menu Clicks
    @Setter(AccessLevel.NONE)
    private @Nullable PlayerSlotClick playerInvClick = null;
    @Setter(AccessLevel.NONE)
    private final Map<Integer, PlayerSlotClick> playerInvClicks = new ConcurrentHashMap<>();

    // Menu Callbacks
    private @Nullable Predicate<InventoryClickEvent> clickPredicate;
    private @Nullable Consumer<InventoryCloseEvent> preCloseConsumer = null;
    private @Nullable Consumer<Player> postCloseConsumer = null;
    private @Nullable MenuOpenCallback openCallback = null;

    // Menu Options
    private final Set<String> ignoredClose = new HashSet<>();
    private boolean allowItemPickup;

    public KamiMenu(@NotNull String name, int lines) {
        this(name, lines, null);
    }
    public KamiMenu(@NotNull String name, @NotNull InventoryType type) {
        this(name, type, null);
    }
    public KamiMenu(@NotNull String name, int lines, @Nullable PageBuilder parent) {
        super(name, lines);
        this.parent = parent;
    }
    public KamiMenu(@NotNull String name, @NotNull InventoryType type, @Nullable PageBuilder parent) {
        super(name, type);
        this.parent = parent;
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
            getIgnoredClose().add(player.getName());
        }

        InventoryView view = Objects.requireNonNull(player.openInventory(this.getInventory()));
        if (openCallback != null) {
            openCallback.onOpen(player, view);
        }
        return view;
    }

    public void closeInventory(@NotNull Player player) {
        closeInventory(player, false);
    }

    public void closeInventory(@NotNull Player player, boolean onlyCloseOne) {
        if (!onlyCloseOne) {
            getIgnoredClose().add(player.getName());
        }

        player.closeInventory();
    }

    public void closeAll() {
        @Nullable Inventory inv = this.inventory;
        if (inv == null) { return; }
        inv.getViewers().forEach(HumanEntity::closeInventory);
    }

    public void whenOpened(@Nullable MenuOpenCallback menuOpen) {
        this.openCallback = menuOpen;
    }



    @NotNull
    @CheckReturnValue
    public IMenuItem addMenuItem(@NotNull IBuilder builder, int slot) {
        return this.addMenuItem(new MenuItem(true, new StaticItemSlot(slot), builder));
    }
    @NotNull
    @CheckReturnValue
    public IMenuItem addMenuItem(@NotNull ItemStack stack, int slot) {
        return this.addMenuItem(new MenuItem(true, new StaticItemSlot(slot), new ItemBuilder(stack)));
    }
    @NotNull
    @CheckReturnValue
    public IMenuItem addMenuItem(@NotNull ConfigurationSection section, @NotNull String key, @Nullable Player player) {
        return this.addMenuItem(MenuItemLoader.load(section.getConfigurationSection(key), player));
    }
    @NotNull
    @CheckReturnValue
    public IMenuItem addMenuItem(@NotNull ConfigurationSection section, @NotNull String key) {
        return this.addMenuItem(MenuItemLoader.load(section.getConfigurationSection(key)));
    }
    @NotNull
    @CheckReturnValue
    public IMenuItem addMenuItem(@NotNull ConfigurationSection section, @Nullable Player player) {
        return this.addMenuItem(MenuItemLoader.load(section, player));
    }
    @NotNull
    @CheckReturnValue
    public IMenuItem addMenuItem(@NotNull ConfigurationSection section) {
        return this.addMenuItem(MenuItemLoader.load(section));
    }

    @NotNull
    public MenuItem addMenuItem(@NotNull MenuItem menuItem) {
        this.menuItems.add(menuItem);
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
        for (MenuItem tickedItem : this.menuItems) {
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
    //                         Player Clicks                        //
    // ------------------------------------------------------------ //

    /**
     * Listen to a player inventory click at a specific slot.
     * @param slot The player inventory slot to listen to.
     */
    @NotNull
    public KamiMenu setPlayerClick(int slot, @NotNull PlayerSlotClick click) {
        this.playerInvClicks.put(slot, click);
        return this;
    }

    /**
     * Listen to all player inventory clicks.
     * @param click The callback to run when a player clicks a slot in their inventory.
     */
    @NotNull
    public KamiMenu setPlayerClick(@NotNull PlayerSlotClick click) {
        this.playerInvClick = click;
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
}
