package com.kamikazejamplugins.kamicommon.gui;

import com.kamikazejamplugins.kamicommon.KamiCommon;
import com.kamikazejamplugins.kamicommon.gui.interfaces.MenuClick;
import com.kamikazejamplugins.kamicommon.gui.interfaces.MenuClickPlayer;
import com.kamikazejamplugins.kamicommon.gui.interfaces.MenuClickPlayerTransform;
import com.kamikazejamplugins.kamicommon.gui.interfaces.MenuClickTransform;
import com.kamikazejamplugins.kamicommon.gui.items.KamiMenuItem;
import com.kamikazejamplugins.kamicommon.item.IAItemBuilder;
import com.kamikazejamplugins.kamicommon.item.IBuilder;
import com.kamikazejamplugins.kamicommon.yaml.ConfigurationSection;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@SuppressWarnings("unused")
public class KamiMenuContainer {
    private String title;
    private int rows;
    @Nullable private KamiMenuItem fillerItem;

    private final Map<String, KamiMenuItem> menuItemMap = new HashMap<>();

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
        if (icons == null) { throw new IllegalArgumentException("No icons section found"); }

        // Load the MenuItems
        for (String key : icons.getKeys(false)) {
            KamiMenuItem item = new KamiMenuItem(icons, key);
            menuItemMap.put(key, item);
        }
    }

    public KamiMenuContainer replaceTitle(String find, String replacement) {
        title = title.replace(find, replacement);
        return this;
    }

    public KamiMenuContainer replaceLore(String key, String find, String replacement) {
        if (!validateKey(key)) { return this; }
        KamiMenuItem item = menuItemMap.get(key);
        item.getIBuilder().replaceLore(find, replacement);
        return this;
    }

    public KamiMenuContainer replaceLoreLine(String key, String find, List<String> replacement) {
        if (!validateKey(key)) { return this; }
        KamiMenuItem item = menuItemMap.get(key);
        item.getIBuilder().replaceLoreLine(find, replacement);
        return this;
    }

    public KamiMenuContainer replaceName(String key, String find, String replacement) {
        if (!validateKey(key)) { return this; }
        KamiMenuItem item = menuItemMap.get(key);
        item.getIBuilder().replaceName(find, replacement);
        return this;
    }

    public KamiMenuItem getItem(String key) {
        if (!validateKey(key)) { return null; }
        return menuItemMap.get(key);
    }



    public KamiMenuContainer addMenuClick(String key, MenuClick click) {
        if (!validateKey(key)) { return this; }
        KamiMenuItem item = menuItemMap.get(key);
        item.setClickInfo(new MenuClickTransform(click));
        return this;
    }

    public KamiMenuContainer addMenuClick(String key, MenuClickPlayer click) {
        if (!validateKey(key)) { return this; }
        KamiMenuItem item = menuItemMap.get(key);
        item.setClickInfo(new MenuClickPlayerTransform(click));
        return this;
    }

    public KamiMenu createKamiMenu() {
        KamiMenu menu = new KamiMenu(title, rows);

        // Regular items
        for (Map.Entry<String, KamiMenuItem> entry : menuItemMap.entrySet()) {
            KamiMenuItem item = entry.getValue();
            int totalSlots = menu.getRows() * 9;
            if (!item.isEnabled() || item.getSlot() < 0 || item.getSlot() >= totalSlots) { continue; }

            menu.addSpecialMenuClick(item.getIBuilder().build(), item.getClickInfo(), item.getSlot());
        }

        // Filler items
        if (fillerItem != null && fillerItem.isEnabled()) {
            menu.fill(fillerItem.getIBuilder());
        }

        return menu;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean validateKey(String key) {
        if (!menuItemMap.containsKey(key)) {
            KamiCommon.get().getLogger().warning("[KamiMenuContainer] Could not find icon key: " + key);
            return false;
        }
        return true;
    }

    public KamiMenuContainer setFillerItem(IBuilder fillerItem) {
        this.fillerItem = new KamiMenuItem(true, fillerItem, -1);
        return this;
    }

    public KamiMenuContainer setFillerItem(ItemStack item) {
        this.fillerItem = new KamiMenuItem(true, new IAItemBuilder(item), -1);
        return this;
    }

    public KamiMenuContainer removeIcon(String key) {
        menuItemMap.remove(key);
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

    public KamiMenuContainer addIcon(String key, ItemStack item, int slot) {
        menuItemMap.put(key, new KamiMenuItem(true, new IAItemBuilder(item), slot));
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

}
