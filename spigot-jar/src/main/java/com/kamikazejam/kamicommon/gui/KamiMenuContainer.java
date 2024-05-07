package com.kamikazejam.kamicommon.gui;

import com.kamikazejam.kamicommon.KamiCommon;
import com.kamikazejam.kamicommon.gui.interfaces.MenuClick;
import com.kamikazejam.kamicommon.gui.interfaces.MenuClickPlayer;
import com.kamikazejam.kamicommon.gui.items.KamiMenuItem;
import com.kamikazejam.kamicommon.gui.page.PageBuilder;
import com.kamikazejam.kamicommon.gui.page.PageItem;
import com.kamikazejam.kamicommon.item.IAItemBuilder;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Getter
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class KamiMenuContainer {
    private String title;
    private int rows;
    @Nullable private KamiMenuItem fillerItem = null;

    private final Map<String, KamiMenuItem> menuItemMap = new HashMap<>();
    private final Map<String, KamiMenuItem> pagedItemMap = new HashMap<>();

    public KamiMenuContainer(String title, int rows) {
        this.title = title;
        this.rows = rows;
    }

    // For calls like new KamiMenuContainer(config, "menus.menu1")
    public KamiMenuContainer(ConfigurationSection section, String key) {
        this(section.getConfigurationSection(key));
    }

    // For calls like new KamiMenuContainer(config.getConfigurationSection("menus.menu1"))
    public KamiMenuContainer(ConfigurationSection section) {
        title = section.getString("title", section.getString("name", " "));
        rows = section.getInt("rows", -1);
        if (rows < 1) { throw new IllegalArgumentException("Invalid rows: " + rows); }

        // Load the Filler item
        boolean fillerEnabled = section.getBoolean("filler.enabled", false);
        if (fillerEnabled) {
            fillerItem = new KamiMenuItem(section.getConfigurationSection("filler"));
        }else {
            fillerItem = null;
        }

        ConfigurationSection icons = section.getConfigurationSection("icons");
        ConfigurationSection pagedIcons = section.getConfigurationSection("pagedIcons");
        // Load the MenuItems
        for (String key : icons.getKeys(false)) {
            KamiMenuItem item = new KamiMenuItem(icons, key);
            menuItemMap.put(key, item);
        }

        // Load the MenuItems
        for (String key : pagedIcons.getKeys(false)) {
            KamiMenuItem item = new KamiMenuItem(pagedIcons, key);
            pagedItemMap.put(key, item);
        }
    }

    public KamiMenuContainer replaceTitle(String find, String replacement) {
        title = title.replace(find, replacement);
        return this;
    }

    public KamiMenuContainer replaceBoth(String key, String find, String replacement) {
        return modifyItem(key, item -> item.getIBuilder().replaceBoth(find, replacement));
    }

    public KamiMenuContainer replaceLore(String key, String find, String replacement) {
        return modifyItem(key, item -> item.getIBuilder().replaceLore(find, replacement));
    }

    public KamiMenuContainer replaceLoreLine(String key, String find, List<String> replacement) {
        return modifyItem(key, item -> item.getIBuilder().replaceLoreLine(find, replacement));
    }

    public KamiMenuContainer replaceName(String key, String find, String replacement) {
        return modifyItem(key, item -> item.getIBuilder().replaceName(find, replacement));
    }

    /**
     * @return A @Nullable KamiMenuItem of the menuItemMap with the given key. <p>
     *         This is a regular menu item, and not from the paged list. (Use {@link #getPagedItem(String)} for that)
     */
    public @Nullable KamiMenuItem getItem(String key) {
        if (!menuItemMap.containsKey(key)) { return null; }
        return menuItemMap.get(key);
    }

    /**
     * @return A @Nullable KamiMenuItem of the pagedItemMap with the given key. <p>
     *         This is a PAGED menu item, not a slot-based item. (Use {@link #getItem(String)} for that)
     */
    public @Nullable KamiMenuItem getPagedItem(String key) {
        if (!pagedItemMap.containsKey(key)) { return null; }
        return pagedItemMap.get(key);
    }

    public KamiMenuContainer addMenuClick(String key, MenuClick click) {
        return modifyItem(key, item -> item.setMenuClick(click));
    }

    public KamiMenuContainer addMenuClick(String key, MenuClickPlayer click) {
        return modifyItem(key, item -> item.setMenuClick(click));
    }

    public void openMenu(Player player) {
        openMenu(player, 0);
    }
    public void openMenu(Player player, int page) {
        createKamiMenu(player, page).openMenu(player);
    }

    public KamiMenu createKamiMenu(Player player) {
        return createKamiMenu(player, 0);
    }

    /**
     * Use if you have established pagedIcons or added paged icons / anticipate pages in this menu
     */
    public KamiMenu createKamiMenu(Player player, int page) {
        PageBuilder<Player> builder = new PageBuilder<>() {
            @Override
            public String getMenuName() { return title; }
            @Override
            public int getRows(int page) { return rows; }



            @Override
            public Collection<? extends PageItem> getItems() {
                return pagedItemMap.values();
            }

            @Override
            public Collection<KamiMenuItem> supplyOtherIcons() {
                return menuItemMap.values();
            }

            @Override
            public KamiMenuItem getFillerIcon() {
                return fillerItem;
            }
        };

        return builder.createMenu(player, page);
    }

    private KamiMenuContainer modifyItem(String key, Consumer<KamiMenuItem> consumer) {
        if (!isValidKey(key)) {
            KamiCommon.get().getLogger().warning("[KamiMenuContainer] Could not find icon key: " + key);
            return this;
        }
        KamiMenuItem menuItem = menuItemMap.get(key);
        if (menuItem != null) {
            consumer.accept(menuItem);
        }

        KamiMenuItem pagedItem = pagedItemMap.get(key);
        if (pagedItem != null) {
            consumer.accept(pagedItem);
        }
        return this;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isValidKey(String key) {
        return menuItemMap.containsKey(key) || pagedItemMap.containsKey(key);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean validateKey(String key) {
        if (!isValidKey(key)) {
            KamiCommon.get().getLogger().warning("[KamiMenuContainer] Could not find icon key: " + key);
            return false;
        }
        return true;
    }

    public KamiMenuContainer setFillerItem(@Nullable IBuilder fillerItem) {
        if (fillerItem == null) { this.fillerItem = null; return this; }

        this.fillerItem = new KamiMenuItem(true, fillerItem, -1);
        return this;
    }

    public KamiMenuContainer setFillerItem(@Nullable ItemStack item) {
        if (item == null) { this.fillerItem = null; return this; }

        this.fillerItem = new KamiMenuItem(true, new IAItemBuilder(item), -1);
        return this;
    }

    public KamiMenuContainer removeIcon(String key) {
        menuItemMap.remove(key);
        return this;
    }
    public KamiMenuContainer removePagedIcon(String key) {
        pagedItemMap.remove(key);
        return this;
    }

    public KamiMenuContainer addIcon(String key, KamiMenuItem item) {
        menuItemMap.put(key, item);
        return this;
    }
    public KamiMenuContainer addIcon(String key, IBuilder item, int slot) {
        menuItemMap.put(key, new KamiMenuItem(true, item, slot));
        return this;
    }
    public KamiMenuContainer addIcon(String key, IBuilder item, List<Integer> slots) {
        menuItemMap.put(key, new KamiMenuItem(true, item, slots));
        return this;
    }
    public KamiMenuContainer addIcon(String key, ItemStack item, int slot) {
        menuItemMap.put(key, new KamiMenuItem(true, new IAItemBuilder(item), slot));
        return this;
    }
    public KamiMenuContainer addIcon(String key, ItemStack item, List<Integer> slots) {
        menuItemMap.put(key, new KamiMenuItem(true, new IAItemBuilder(item), slots));
        return this;
    }


    public KamiMenuContainer addPagedIcon(String key, KamiMenuItem item) {
        pagedItemMap.put(key, item);
        return this;
    }
    public KamiMenuContainer addPagedIcon(String key, IBuilder item, int slot) {
        pagedItemMap.put(key, new KamiMenuItem(true, item, slot));
        return this;
    }
    public KamiMenuContainer addPagedIcon(String key, IBuilder item, List<Integer> slots) {
        pagedItemMap.put(key, new KamiMenuItem(true, item, slots));
        return this;
    }
    public KamiMenuContainer addPagedIcon(String key, ItemStack item, int slot) {
        pagedItemMap.put(key, new KamiMenuItem(true, new IAItemBuilder(item), slot));
        return this;
    }
    public KamiMenuContainer addPagedIcon(String key, ItemStack item, List<Integer> slots) {
        pagedItemMap.put(key, new KamiMenuItem(true, new IAItemBuilder(item), slots));
        return this;
    }





    public KamiMenuContainer setTitle(String title) {
        this.title = title;
        return this;
    }

    public KamiMenuContainer setRows(int rows) {
        this.rows = rows;
        return this;
    }

    public KamiMenuContainer setPlayerHeadOwner(String key, Player player) {
        return this.setPlayerHeadOwner(key, player.getName());
    }

    public KamiMenuContainer setPlayerHeadOwner(String key, String playerName) {
        return modifyItem(key, item -> item.getIBuilder().setSkullOwner(playerName));
    }
}
