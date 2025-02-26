package com.kamikazejam.kamicommon.menu.api.loaders.menu;

import com.kamikazejam.kamicommon.menu.OneClickMenu;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.slots.IconSlot;
import com.kamikazejam.kamicommon.menu.api.loaders.IconSlotLoader;
import com.kamikazejam.kamicommon.menu.api.loaders.MenuIconLoader;
import com.kamikazejam.kamicommon.menu.api.loaders.MenuSizeLoader;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for loading a {@link OneClickMenu.Builder} from a {@link ConfigurationSection}.
 */
@SuppressWarnings({"unused", "DuplicatedCode"})
public class OneClickMenuLoader {
    /**
     * Loads a {@link OneClickMenu.Builder} from a {@link ConfigurationSection} with the given key.<br>
     * Identical to calling {@link #loadMenu(ConfigurationSection)} with {@link ConfigurationSection#getConfigurationSection(String key)}
     * @return A new {@link OneClickMenu.Builder} instance with data (title, size, icons, etc.) loaded from the config.
     */
    public static @NotNull OneClickMenu.Builder loadMenu(@NotNull ConfigurationSection section, @NotNull String key) {
        return loadMenu(section.getConfigurationSection(key));
    }

    /**
     * Loads a {@link OneClickMenu.Builder} from a {@link ConfigurationSection}.
     * @return A new {@link OneClickMenu.Builder} instance with data (title, size, icons, etc.) loaded from the config.
     */
    public static @NotNull OneClickMenu.Builder loadMenu(@NotNull ConfigurationSection section) {
        // Load title from 'title' or 'name', defaulting to " "
        String title = section.getString("title", section.getString("name", " "));
        OneClickMenu.Builder builder = new OneClickMenu.Builder(MenuSizeLoader.load(section)).title(StringUtil.t(title));

        // Load Filler Icon
        if (section.isConfigurationSection("filler")) {
            builder.fillerIcon(MenuIconLoader.load(section.getConfigurationSection("filler")));
        }

        // Load Icons
        ConfigurationSection icons = section.getConfigurationSection("icons");
        for (String key : icons.getKeys(false)) {
            builder.modifyIcons((access) -> {
                ConfigurationSection iconSection = icons.getConfigurationSection(key);
                MenuIcon icon = MenuIconLoader.load(iconSection).setId(key);
                IconSlot slot = IconSlotLoader.load(iconSection);
                access.setMenuIcon(icon, slot);
            });
        }

        return builder;
    }
}
