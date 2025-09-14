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
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.util.LegacyColors;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

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
     * Identifies the type of title string being used, and tries its best to set it correctly on the builder.<br>
     * Supports (parsed in this order):<br>
     * - Legacy Section (contains &sect; symbols)<br>
     * - MiniMessage (contains &lt;tag&gt; tags)<br>
     * - Legacy Ampersand (contains &amp; symbols)<br>
     */
    public static <M extends Menu<M>, T extends AbstractMenuBuilder<M, T>> @NotNull AbstractMenuBuilder<M, T> setTitle(
            @NotNull AbstractMenuBuilder<M, T> builder,
            @NotNull String titleString
    ) {
        // 1. MiniMessage cannot support &sect; symbols, so if we find one, it's definitely legacy
        if (titleString.contains("§")) {
            // auto translate (to maintain previous behavior)
            return builder.titleFromLegacySection(LegacyColors.t(titleString));
        }
        // 2. If it contains <tag> symbols, it's most likely MiniMessage
        Pattern pattern = Pattern.compile("<[^<>]+>");
        if (titleString.contains("<\\") || pattern.matcher(titleString).find()) {
            return builder.title((ComponentMenuTitleProvider) (player) ->
                    NmsAPI.getVersionedComponentSerializer().fromMiniMessage(titleString)
            );
        }
        // 3. If it contains & symbols, it's most likely legacy ampersand
        if (titleString.contains("&")) {
            // auto translate (to maintain previous behavior)
            String translated = LegacyColors.t(titleString);
            return builder.title((ComponentMenuTitleProvider) (player) ->
                    NmsAPI.getVersionedComponentSerializer().fromLegacySection(translated)
            );
        }
        // 4. Otherwise, just treat it as plain text
        // (can use the mini message parses since it won't error on plain text, it just won't do anything special)
        return builder.title((ComponentMenuTitleProvider) (player) ->
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage(titleString)
        );
    }
}
