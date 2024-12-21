package com.kamikazejam.kamicommon.gui.loader;

import com.kamikazejam.kamicommon.gui.KamiMenu;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for loading a {@link KamiMenu} from a {@link ConfigurationSection}.
 */
@SuppressWarnings("unused")
public class KamiMenuLoader {
    /**
     * Loads a {@link KamiMenu} from a {@link ConfigurationSection} with the given key.<br>
     * Identical to calling {@link #loadMenu(ConfigurationSection)} with {@link ConfigurationSection#getConfigurationSection(String key)}
     * @return A new {@link KamiMenu} instance with data (title, size, items, etc.) loaded from the config.
     */
    public static @NotNull KamiMenu loadMenu(@NotNull ConfigurationSection section, @NotNull String key) {
        return loadMenu(section.getConfigurationSection(key));
    }

    /**
     * Loads a {@link KamiMenu} from a {@link ConfigurationSection}.
     * @return A new {@link KamiMenu} instance with data (title, size, items, etc.) loaded from the config.
     */
    public static @NotNull KamiMenu loadMenu(@NotNull ConfigurationSection section) {
        // Load title from 'title' or 'name', defaulting to " "
        String title = section.getString("title", section.getString("name", " "));
        KamiMenu menu = new KamiMenu(title, MenuSizeLoader.load(section));

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
