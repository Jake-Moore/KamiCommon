package com.kamikazejam.kamicommon.menu.api.loaders;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.SpigotUtilsSource;
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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
            String listKey = m1 ? "materials" : "material";
            // Parse out only the valid ones into XMaterial types
            return mapStringsToItemBuilders(section, player, listKey, section.getStringList(listKey));
        }

        boolean t1 = section.isList("types");
        boolean t2 = section.isList("type");
        if (t1 || t2) {
            String listKey = t1 ? "types" : "type";
            return mapStringsToItemBuilders(section, player, listKey, section.getStringList(listKey));
        }

        // Method2: Default to single item logic
        ItemBuilder builder = ItemBuilderLoader.load(section);
        if (player != null) { builder.setSkullOwner(player.getName()); }
        return Collections.singletonList(builder);
    }

    private static @NotNull List<ItemBuilder> mapStringsToItemBuilders(
            @NotNull ConfigurationSection section,
            @Nullable OfflinePlayer player,
            @NotNull String listKey,
            @NotNull List<String> mats
    ) {
        // The legacy 'data' value describes the icon, not one entry of the list, and it was
        // hardcoded to null here, so 'data:' was ignored for every multi-material icon. "WOOL"
        // with "data: 14" got whichever wool colour XMaterial resolves first instead of red.
        // Read it exactly the way ItemTypeLoader.loadType does for the single-material form.
        @Nullable Integer data = section.isInt("data") ? section.getInt("data") : null;

        String path = section.getCurrentPath();
        String where = ((path == null || path.isEmpty()) ? "(config root)" : path) + "." + listKey;

        List<ItemBuilder> builders = new ArrayList<>();
        for (String str : mats) {
            // A typo in the single 'material:' form throws IllegalArgumentException. In the list
            // form it used to fall through two filter(Objects::nonNull) stages, so the icon came
            // back with fewer materials than the config named and nothing said so. Keep dropping
            // the entry, because throwing would break configs that load today, but name the path
            // and the value that was dropped.
            @Nullable XMaterial type = ItemTypeLoader.loadTypeByString(str, data);
            if (type == null) {
                SpigotUtilsSource.warning("Unknown material '" + str + "' at " + where
                        + ", so that entry was skipped. Check it against the XMaterial names.");
                continue;
            }
            @Nullable ItemStack stack = type.parseItem();
            if (stack == null) {
                SpigotUtilsSource.warning("Material '" + str + "' at " + where + " resolved to "
                        + type.name() + ", which does not exist on this server version, so that "
                        + "entry was skipped.");
                continue;
            }
            ItemBuilder builder = ItemBuilderLoader.loadPatches(stack, section);
            if (player != null) { builder.setSkullOwner(player.getName()); }
            builders.add(builder);
        }
        return builders;
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
