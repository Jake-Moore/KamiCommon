package com.kamikazejam.kamicommon.menu.api.loaders;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.configuration.Configurable;
import com.kamikazejam.kamicommon.configuration.loader.ItemTypeLoader;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilderLoader;
import com.kamikazejam.kamicommon.menu.Menu;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

// NOTE: This class does not set the MenuIcon<M> ID to the section key, that is handled only if we're loading a MenuIcon<M> for a specific Menu
// I.E. in the SimpleMenuLoader class, which will update the id when loading keys from the 'icons' section
public class MenuIconLoader {
    @NotNull
    public static <M extends Menu<M>> MenuIcon<M> load(@NotNull ConfigurationSection section) {
        return load(section, null);
    }

    @NotNull
    public static <M extends Menu<M>> MenuIcon<M> load(@NotNull ConfigurationSection section, @Nullable OfflinePlayer player) {
        boolean enabled = section.getBoolean("enabled", true);

        // Load the ItemBuilders
        Collection<ItemBuilder> itemBuilders = loadItemBuilders(section, player);

        // Toggle attributes so all Menu Icons have a clean look by default (configurable via Config class)
        if ((!section.isSet("hide-attributes") || !section.isBoolean("hide-attributes")) && Config.isHideIconAttributes()) {
            // There was not a config override supplied, so apply the default behavior
            itemBuilders.forEach(ItemBuilder::hideAttributes);
        }

        // Create the MenuIcon
        MenuIcon<M> icon = new MenuIcon<>(enabled, itemBuilders);

        // Apply additional settings
        if (section.isSet("typeCycleTicks")) {
            icon.setBuilderRotateTicks(section.getInt("typeCycleTicks"));
        }

        return icon;
    }

    @NotNull
    private static Collection<ItemBuilder> loadItemBuilders(@NotNull ConfigurationSection section, @Nullable OfflinePlayer player) {

        // Method1: Try to Load multiple materials/types (from any of the 4 allowed keys)
        boolean m1 = section.isList("materials");
        boolean m2 = section.isList("material");
        if (m1 || m2) {
            // Fetch the list of material names
            List<String> mats = (m1) ? section.getStringList("materials") : section.getStringList("material");
            // Parse out only the valid ones into XMaterial types
            return mapStringsToItemBuilders(section, player, mats);
        }

        boolean t1 = section.isList("types");
        boolean t2 = section.isList("type");
        if (t1 || t2) {
            List<String> mats = (t1) ? section.getStringList("types") : section.getStringList("type");
            return mapStringsToItemBuilders(section, player, mats);
        }

        // Method2: Default to single item logic
        ItemBuilder builder = ItemBuilderLoader.load(section);
        if (player != null) { builder.setSkullOwner(player.getName()); }
        return Collections.singletonList(builder);
    }

    private static @NotNull List<ItemBuilder> mapStringsToItemBuilders(@NotNull ConfigurationSection section, @Nullable OfflinePlayer player, List<String> mats) {
        return mats.stream()
                .map(str -> ItemTypeLoader.loadTypeByString(str, null))
                .filter(Objects::nonNull) // filter out invalid strings that did not map to an XMaterial
                .map(XMaterial::parseItem)
                .filter(Objects::nonNull) // filter out invalid XMaterial that did not map to an ItemStack
                .map(stack -> {
                    ItemBuilder builder = ItemBuilderLoader.loadPatches(stack, section);
                    if (player != null) {builder.setSkullOwner(player.getName());}
                    return builder;
                }).toList();
    }

    @Configurable
    public static class Config {
        /**
         * When enabled, all {@link ItemBuilder} instances loaded for every {@link MenuIcon} will have their attributes hidden by default.<br>
         * <br>
         * This is equivalent to calling {@link ItemBuilder#hideAttributes()} on every {@link ItemBuilder} instance loaded for every {@link MenuIcon}.<br>
         * <br>
         * This default behavior can be overridden on a per-icon basis by setting the 'hide-attributes' key in the icon's configuration section.
         */
        @Getter @Setter
        private static boolean hideIconAttributes = true;
    }
}
