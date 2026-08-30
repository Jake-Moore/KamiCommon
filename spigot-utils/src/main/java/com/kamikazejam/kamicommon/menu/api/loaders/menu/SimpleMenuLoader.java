package com.kamikazejam.kamicommon.menu.api.loaders.menu;

import com.kamikazejam.kamicommon.menu.AbstractMenuBuilder;
import com.kamikazejam.kamicommon.menu.Menu;
import com.kamikazejam.kamicommon.menu.SimpleMenu;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.slots.IconSlot;
import com.kamikazejam.kamicommon.menu.api.loaders.IconSlotLoader;
import com.kamikazejam.kamicommon.menu.api.loaders.MenuIconLoader;
import com.kamikazejam.kamicommon.menu.api.loaders.MenuSizeLoader;
import com.kamikazejam.kamicommon.menu.api.title.ComponentMenuTitleProvider;
import com.kamikazejam.kamicommon.util.ColoredStringParser;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for loading a {@link SimpleMenu.Builder} from a {@link ConfigurationSection}.
 */
@SuppressWarnings({"unused", "DuplicatedCode"})
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
        // Validate the icons block FIRST, before anything is constructed.
        //
        // getConfigurationSection is @NotNull and hands back an EMPTY section for a key that is not
        // there, so the fetch below never threw: a menu with no 'icons:' block, or with the block
        // misspelled or mis-indented, loaded as a menu with zero icons and opened as an empty
        // inventory, with nothing pointing at the config. Fail here instead, naming the section and
        // the key. Checking before the builder exists also means this is reachable without a
        // running server, which is what makes it testable.
        if (!section.isConfigurationSection("icons")) {
            String path = section.getCurrentPath();
            String where = (path == null || path.isEmpty()) ? "(config root)" : path;
            throw new IllegalArgumentException(
                    "Menu section '" + where + "' has no 'icons' block. Expected a mapping at '"
                            + where + ".icons' holding one subsection per icon."
            );
        }

        // Load title from 'title' or 'name', defaulting to " "
        String title = section.getString("title", section.getString("name", " "));
        SimpleMenu.Builder builder = (SimpleMenu.Builder) setTitle(new SimpleMenu.Builder(MenuSizeLoader.load(section)), title);

        // Load Filler Icon
        if (section.isConfigurationSection("filler")) {
            builder.fillerIcon(MenuIconLoader.load(section.getConfigurationSection("filler")));
        }

        // Load Icons
        ConfigurationSection icons = section.getConfigurationSection("icons");
        for (String key : icons.getKeys(false)) {
            builder.modifyIcons((access) -> {
                ConfigurationSection iconSection = icons.getConfigurationSection(key);
                MenuIcon<SimpleMenu> icon = MenuIconLoader.load(iconSection);
                icon.setId(key);
                IconSlot slot = IconSlotLoader.load(iconSection);
                access.setMenuIcon(icon, slot);
            });
        }

        return builder;
    }

    /**
     * See {@link ColoredStringParser#parse(String)} for parsing details.
     */
    public static <M extends Menu<M>, T extends AbstractMenuBuilder<M, T>> @NotNull AbstractMenuBuilder<M, T> setTitle(
            @NotNull AbstractMenuBuilder<M, T> builder,
            @NotNull String titleString
    ) {
        return builder.title((ComponentMenuTitleProvider) (player) -> ColoredStringParser.parse(titleString));
    }
}
