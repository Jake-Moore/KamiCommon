package com.kamikazejam.kamicommon.menu.api.loaders;

import com.kamikazejam.kamicommon.menu.simple.SimpleMenu;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for loading a {@link SimpleMenu.Builder} from a {@link ConfigurationSection}.
 */
@SuppressWarnings("unused")

// TODO CREATE LOADER FOR PaginatedMenu
public class SimpleMenuLoader {
    /**
     * Loads a {@link SimpleMenu.Builder} from a {@link ConfigurationSection} with the given key.<br>
     * Identical to calling {@link #loadMenu(ConfigurationSection)} with {@link ConfigurationSection#getConfigurationSection(String key)}
     * @return A new {@link SimpleMenu.Builder} instance with data (title, size, icons, etc.) loaded from the config.
     */
    public static @NotNull SimpleMenu.Builder loadMenu(@NotNull ConfigurationSection section, @NotNull String key) {
        return loadMenu(section.getConfigurationSection(key));
    }

    /**
     * Loads a {@link SimpleMenu.Builder} from a {@link ConfigurationSection}.
     * @return A new {@link SimpleMenu.Builder} instance with data (title, size, icons, etc.) loaded from the config.
     */
    public static @NotNull SimpleMenu.Builder loadMenu(@NotNull ConfigurationSection section) {
        // Load title from 'title' or 'name', defaulting to " "
        String title = section.getString("title", section.getString("name", " "));
        SimpleMenu.Builder builder = new SimpleMenu.Builder(MenuSizeLoader.load(section)).title(StringUtil.t(title));

        // Load Filler Icon
        if (section.isConfigurationSection("filler")) {
            builder.fillerIcon(MenuIconLoader.load(section.getConfigurationSection("filler")));
        }

        // Load Icons
        ConfigurationSection icons = section.getConfigurationSection("icons");
        for (String key : icons.getKeys(false)) {
            builder.modifyIcons((access) -> {
                access.addMenuIcon(MenuIconLoader.load(icons.getConfigurationSection(key)).setId(key));
            });
        }

        return builder;
    }
}
