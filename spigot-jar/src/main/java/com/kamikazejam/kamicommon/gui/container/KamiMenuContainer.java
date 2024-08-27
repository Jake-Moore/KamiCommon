package com.kamikazejam.kamicommon.gui.container;

import com.kamikazejam.kamicommon.PluginSource;
import com.kamikazejam.kamicommon.gui.KamiMenu;
import com.kamikazejam.kamicommon.gui.clicks.MenuClick;
import com.kamikazejam.kamicommon.gui.clicks.MenuClickEvent;
import com.kamikazejam.kamicommon.gui.clicks.MenuClickPage;
import com.kamikazejam.kamicommon.gui.items.MenuItem;
import com.kamikazejam.kamicommon.gui.items.interfaces.IBuilderModifier;
import com.kamikazejam.kamicommon.gui.loader.MenuItemLoader;
import com.kamikazejam.kamicommon.gui.page.PageBuilder;
import com.kamikazejam.kamicommon.item.IAItemBuilder;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.util.StringUtilP;
import com.kamikazejam.kamicommon.xseries.XMaterial;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

@Getter @Accessors(chain = true)
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class KamiMenuContainer {
    @Getter @AllArgsConstructor
    public static class IndexedItem {
        private final MenuItem item;
        private final int index;
    }

    private String title;
    private @Nullable Integer rows;         // Accepted Values: [-1, 1, 2, 3, 4, 5, 6], or [-1, 4, 5, 6] for paginated menus
    private @Nullable InventoryType type;
    @Nullable private MenuItem fillerItem = null;
    @Setter
    private boolean ordered = false;

    @Getter(AccessLevel.NONE)
    private final Map<String, MenuItem> menuItemMap = new HashMap<>();
    @Getter(AccessLevel.NONE)
    private final Map<String, IndexedItem> pagedItemMap = new HashMap<>();

    public KamiMenuContainer(@NotNull String title, int rows) {
        this.title = title;
        this.rows = rows;
        this.type = null;
    }

    public KamiMenuContainer(@NotNull String title, @NotNull InventoryType type) {
        this.title = title;
        this.type = type;
        this.rows = null;
    }

    // For calls like new KamiMenuContainer(config, "menus.menu1")
    public KamiMenuContainer(@NotNull ConfigurationSection section, @NotNull String key) {
        this(section.getConfigurationSection(key));
    }

    // For calls like new KamiMenuContainer(config.getConfigurationSection("menus.menu1"))
    public KamiMenuContainer(@NotNull ConfigurationSection section) {
        title = section.getString("title", section.getString("name", " "));

        // Load Inventory Type/Size
        this.loadInventorySize(section);

        // Load the Filler item
        if (section.isConfigurationSection("filler")) {
            if (section.isSet("filler.enabled") && !section.getBoolean("filler.enabled", false)) {
                // Case 1. `filler.enabled` set to false (use no filler)
                fillerItem = null;
            }else {
                // Case 2. `filler.enabled` is unset or true (use the provided filler)
                fillerItem = MenuItemLoader.load(section.getConfigurationSection("filler"));
            }
        }else {
            // Case 3. `filler` section not provided, use the default
            fillerItem = defaultFiller;
        }

        ConfigurationSection icons = section.getConfigurationSection("icons");
        ConfigurationSection pagedIcons = section.getConfigurationSection("pagedIcons");
        // Load the normal icons
        for (String key : icons.getKeys(false)) {
            menuItemMap.put(key, MenuItemLoader.load(icons.getConfigurationSection(key)));
        }

        // Load the paged icons
        for (String key : pagedIcons.getKeys(false)) {
            MenuItem item = MenuItemLoader.load(pagedIcons.getConfigurationSection(key));
            pagedItemMap.put(key, new IndexedItem(item, pagedItemMap.size()));
        }
    }

    private void loadInventorySize(@NotNull ConfigurationSection section) {
        // Load Type with higher priority (if successful -> return)
        if (section.isString("type")) {
            String s = section.getString("type");
            try {
                type = InventoryType.valueOf(s);
                rows = null;
                return;
            }catch (IllegalArgumentException ignored) {}
        }

        // Try to load rows
        if (section.isInt("rows")) {
            int r = section.getInt("rows");
            if (r > 0 && r < 7) {
                rows = r;
                type = null;
                return;
            }
        }
        if (section.isInt("row")) {
            int r = section.getInt("row");
            if (r > 0 && r < 7) {
                rows = r;
                type = null;
                return;
            }
        }

        // Give them more details about their configuration errors
        if (section.isString("type")) {
            PluginSource.get().getLogger().warning("Invalid inventory type in config: '" + section.getString("type") + "' at " + (section.getCurrentPath() + ".type"));
        }
        if (section.isInt("rows")) {
            PluginSource.get().getLogger().warning("Invalid inventory rows in config: '" + section.getInt("rows") + "' at " + (section.getCurrentPath() + ".rows"));
        }
        if (section.isInt("row")) {
            PluginSource.get().getLogger().warning("Invalid inventory row in config: '" + section.getInt("row") + "' at " + (section.getCurrentPath() + ".row"));
        }

        throw new IllegalStateException("Invalid inventory size (rows or type) in config at " + section.getCurrentPath());
    }

    @NotNull
    public KamiMenuContainer replaceTitle(@NotNull String find, @NotNull String replacement) {
        Preconditions.checkNotNull(find, "find cannot be null");
        Preconditions.checkNotNull(replacement, "replacement cannot be null");
        title = title.replace(find, replacement);
        return this;
    }

    /**
     * @return A nullable {@link MenuItem} of the menuItemMap with the given key. <p>
     *         This is a regular menu item, and not from the paged list. (Use {@link #getPagedItem(String)} for that)
     */
    public @Nullable MenuItem getItem(@NotNull String key) {
        if (!menuItemMap.containsKey(key)) { return null; }
        return menuItemMap.get(key);
    }

    /**
     * @return A @Nullable {@link MenuItem} of the pagedItemMap with the given key. <p>
     *         This is a PAGED menu item, not a slot-based item. (Use {@link #getItem(String)} for that)
     */
    public @Nullable MenuItem getPagedItem(@NotNull String key) {
        if (!pagedItemMap.containsKey(key)) { return null; }
        return pagedItemMap.get(key).item;
    }

    @NotNull
    public KamiMenuContainer setMenuClick(@NotNull String key, @NotNull MenuClick click) {
        return modifyItem(key, item -> item.setMenuClick(click));
    }
    @NotNull
    public KamiMenuContainer setMenuClick(@NotNull String key, @NotNull MenuClickPage click) {
        return modifyItem(key, item -> item.setMenuClick(click));
    }
    @NotNull
    public KamiMenuContainer setMenuClick(@NotNull String key, @NotNull MenuClickEvent click) {
        return modifyItem(key, item -> item.setMenuClick(click));
    }
    @NotNull
    public KamiMenuContainer setModifier(@NotNull String key, @NotNull IBuilderModifier modifier) {
        return modifyItem(key, item -> item.setModifier(modifier));
    }
    @NotNull
    public KamiMenuContainer setAutoUpdate(@NotNull String key, @NotNull IBuilderModifier modifier, int tickInterval) {
        return modifyItem(key, item -> item.setAutoUpdate(modifier, tickInterval));
    }

    public void openMenu(@NotNull Player player) {
        openMenu(player, 0);
    }
    public void openMenu(@NotNull Player player, int page) {
        createKamiMenu(player, page).openMenu(player);
    }

    @NotNull
    public KamiMenu createKamiMenu(@NotNull Player player) {
        return createKamiMenu(player, 0);
    }

    @NotNull
    public KamiMenu createKamiMenu(@NotNull Player player, int page) {
        PageBuilder builder = new PageBuilder() {
            @Override
            public String getMenuName() { return StringUtilP.justP(player, title); }

            @Override
            public @NotNull KamiMenu createBlankMenu(@NotNull String title, int page) {
                if (type != null) {
                    return new KamiMenu(title, type, this);
                }else if (rows != null) {
                    return new KamiMenu(title, rows, this);
                }
                // Should not happen, our constructor doesn't allow it
                throw new IllegalStateException("Invalid rows or type");
            }

            @Override
            public Collection<MenuItem> getItems() {
                if (!ordered) {
                    return pagedItemMap.values().stream().map(IndexedItem::getItem).toList();
                }
                // Sort by the order they were added to the map (the index)
                List<IndexedItem> items = new ArrayList<>(pagedItemMap.values());
                items.sort(Comparator.comparingInt(IndexedItem::getIndex));
                return items.stream().map(IndexedItem::getItem).toList();
            }

            @Override
            public Collection<MenuItem> supplyOtherIcons() {
                return menuItemMap.values();
            }

            @Override
            public MenuItem getFillerIcon() {
                return fillerItem;
            }
        };

        return builder.createMenu(player, page);
    }

    @NotNull
    private KamiMenuContainer modifyItem(@NotNull String key, @NotNull Consumer<MenuItem> consumer) {
        if (!isValidKey(key)) {
            PluginSource.warning("[KamiMenuContainer] Could not find icon key: " + key);
            return this;
        }
        MenuItem menuItem = menuItemMap.get(key);
        if (menuItem != null) {
            consumer.accept(menuItem);
        }

        IndexedItem pagedItem = pagedItemMap.get(key);
        if (pagedItem != null) {
            consumer.accept(pagedItem.getItem());
        }
        return this;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isValidKey(@NotNull String key) {
        return menuItemMap.containsKey(key) || pagedItemMap.containsKey(key);
    }
    public boolean isValidIcon(@NotNull String key) {
        return menuItemMap.containsKey(key);
    }
    public boolean isValidPagedIcon(@NotNull String key) {
        return pagedItemMap.containsKey(key);
    }

    @NotNull
    public Set<String> getAllIconKeys() {
        Set<String> keys = new HashSet<>();
        keys.addAll(menuItemMap.keySet());
        keys.addAll(pagedItemMap.keySet());
        return keys;
    }
    @NotNull
    public Set<String> getIconKeys() {
        return menuItemMap.keySet();
    }
    @NotNull
    public Set<String> getPagedIconKeys() {
        return pagedItemMap.keySet();
    }

    @NotNull
    public KamiMenuContainer setFillerItem(@Nullable IBuilder fillerItem) {
        if (fillerItem == null) {
            this.fillerItem = null;
            return this;
        }

        this.fillerItem = new MenuItem(true, fillerItem, -1);
        return this;
    }

    @NotNull
    public KamiMenuContainer setFillerItem(@Nullable ItemStack item) {
        if (item == null) {
            this.fillerItem = null;
            return this;
        }

        this.fillerItem = new MenuItem(true, new IAItemBuilder(item), -1);
        return this;
    }

    @NotNull
    public KamiMenuContainer removeIcon(@NotNull String key) {
        menuItemMap.remove(key);
        return this;
    }
    @NotNull
    public KamiMenuContainer removePagedIcon(@NotNull String key) {
        pagedItemMap.remove(key);
        return this;
    }

    @NotNull
    public KamiMenuContainer addIcon(@NotNull String key, @NotNull MenuItem item) {
        menuItemMap.put(key, item);
        return this;
    }
    @NotNull
    public KamiMenuContainer addIcon(@NotNull String key, @NotNull IBuilder item, int slot) {
        menuItemMap.put(key, new MenuItem(true, item, slot));
        return this;
    }
    @NotNull
    public KamiMenuContainer addIcon(@NotNull String key, @NotNull IBuilder item, @NotNull List<Integer> slots) {
        menuItemMap.put(key, new MenuItem(true, item, slots));
        return this;
    }
    @NotNull
    public KamiMenuContainer addIcon(@NotNull String key, @NotNull ItemStack item, int slot) {
        menuItemMap.put(key, new MenuItem(true, new IAItemBuilder(item), slot));
        return this;
    }
    @NotNull
    public KamiMenuContainer addIcon(@NotNull String key, @NotNull ItemStack item, @NotNull List<Integer> slots) {
        menuItemMap.put(key, new MenuItem(true, new IAItemBuilder(item), slots));
        return this;
    }


    @NotNull
    public KamiMenuContainer addPagedIcon(@NotNull String key, @NotNull MenuItem item) {
        pagedItemMap.put(key, new IndexedItem(item, pagedItemMap.size()));
        return this;
    }
    @NotNull
    public KamiMenuContainer addPagedIcon(@NotNull String key, @NotNull IBuilder item) {
        pagedItemMap.put(key, new IndexedItem(new MenuItem(true, item, -1), pagedItemMap.size()));
        return this;
    }
    @NotNull
    public KamiMenuContainer addPagedIcon(@NotNull String key, @NotNull IBuilder item, int slot) {
        pagedItemMap.put(key, new IndexedItem(new MenuItem(true, item, slot), pagedItemMap.size()));
        return this;
    }
    @NotNull
    public KamiMenuContainer addPagedIcon(@NotNull String key, @NotNull IBuilder item, @NotNull List<Integer> slots) {
        pagedItemMap.put(key, new IndexedItem(new MenuItem(true, item, slots), pagedItemMap.size()));
        return this;
    }
    @NotNull
    public KamiMenuContainer addPagedIcon(@NotNull String key, @NotNull ItemStack item, int slot) {
        pagedItemMap.put(key, new IndexedItem(new MenuItem(true, new IAItemBuilder(item), slot), pagedItemMap.size()));
        return this;
    }
    @NotNull
    public KamiMenuContainer addPagedIcon(@NotNull String key, @NotNull ItemStack item, @NotNull List<Integer> slots) {
        pagedItemMap.put(key, new IndexedItem(new MenuItem(true, new IAItemBuilder(item), slots), pagedItemMap.size()));
        return this;
    }

    @NotNull
    public KamiMenuContainer setTitle(@NotNull String title) {
        this.title = title;
        return this;
    }

    @NotNull
    public KamiMenuContainer setRows(int rows) {
        this.rows = rows;
        this.type = null;
        return this;
    }

    @NotNull
    public KamiMenuContainer setType(@Nullable InventoryType type) {
        this.type = type;
        this.rows = null;
        return this;
    }

    @NotNull
    public KamiMenuContainer setPlayerHeadOwner(@NotNull String key, @NotNull Player player) {
        return this.setPlayerHeadOwner(key, player.getName());
    }

    @NotNull
    public KamiMenuContainer setPlayerHeadOwner(String key, String playerName) {
        return modifyItem(key, item -> item.directModifyBuilders((builder) -> builder.setSkullOwner(playerName)));
    }

    // Configure the default filler icon
    public static MenuItem defaultFiller = new MenuItem(
            true,
            new IAItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE)
                    .setName("&7")
                    .setLore(),
            -1
    );
}
