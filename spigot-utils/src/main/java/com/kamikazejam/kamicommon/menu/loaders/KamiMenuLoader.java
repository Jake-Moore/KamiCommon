package com.kamikazejam.kamicommon.menu.loaders;

import com.kamikazejam.kamicommon.menu.OLD_KAMI_MENU;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for loading a {@link OLD_KAMI_MENU} from a {@link ConfigurationSection}.
 */
@SuppressWarnings("unused")
public class KamiMenuLoader {
    /**
     * Loads a {@link OLD_KAMI_MENU} from a {@link ConfigurationSection} with the given key.<br>
     * Identical to calling {@link #loadMenu(ConfigurationSection)} with {@link ConfigurationSection#getConfigurationSection(String key)}
     * @return A new {@link OLD_KAMI_MENU} instance with data (title, size, items, etc.) loaded from the config.
     */
    public static @NotNull OLD_KAMI_MENU loadMenu(@NotNull ConfigurationSection section, @NotNull String key) {
        return loadMenu(section.getConfigurationSection(key));
    }

    /**
     * Loads a {@link OLD_KAMI_MENU} from a {@link ConfigurationSection}.
     * @return A new {@link OLD_KAMI_MENU} instance with data (title, size, items, etc.) loaded from the config.
     */
    public static @NotNull OLD_KAMI_MENU loadMenu(@NotNull ConfigurationSection section) {
        // Load title from 'title' or 'name', defaulting to " "
        String title = section.getString("title", section.getString("name", " "));
        OLD_KAMI_MENU menu = new OLD_KAMI_MENU(title, MenuSizeLoader.load(section));

        // Load Filler Item
        if (section.isConfigurationSection("filler")) {
            menu.setFillerItem(MenuItemLoader.load(section.getConfigurationSection("filler")));
        }

        // Load Icons
        ConfigurationSection icons = section.getConfigurationSection("icons");
        for (String key : icons.getKeys(false)) {
            menu.addMenuItem(MenuItemLoader.load(icons.getConfigurationSection(key)).setId(key));
        }

        return menu;
    }
}
