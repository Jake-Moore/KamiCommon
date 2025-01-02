package com.kamikazejam.kamicommon.menu.api.loaders;

import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.slots.IconSlot;
import com.kamikazejam.kamicommon.menu.api.icons.slots.LastRowIconSlot;
import com.kamikazejam.kamicommon.menu.api.icons.slots.StaticIconSlot;
import com.kamikazejam.kamicommon.item.IAItemBuilder;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

// NOTE: This class does not set the MenuIcon ID to the section key, that is handled only if we're loading a MenuIcon for a specific Menu
// I.E. in the SimpleMenuLoader class, which will update the id when loading keys from the 'icons' section
public class MenuIconLoader {
    @NotNull
    public static MenuIcon load(@NotNull ConfigurationSection section) {
        return load(section, null);
    }

    @NotNull
    public static MenuIcon load(@NotNull ConfigurationSection section, @Nullable OfflinePlayer player) {
        boolean enabled = section.getBoolean("enabled", true);

        // Load the IBuilders
        Collection<IBuilder> iBuilders = loadIBuilders(section, player);
        if (section.getBoolean("hideAttributes", true)) { iBuilders.forEach(IBuilder::hideAttributes); }

        // Create the MenuIcon
        MenuIcon icon = new MenuIcon(enabled, iBuilders);

        // Apply additional settings
        if (section.isSet("typeCycleTicks")) {
            icon.setBuilderRotateTicks(section.getInt("typeCycleTicks"));
        }

        return icon;
    }

    @NotNull
    private static Collection<IBuilder> loadIBuilders(@NotNull ConfigurationSection section, @Nullable OfflinePlayer player) {

        // Method1: Try to Load multiple materials/types (from any of the 4 allowed keys)
        boolean m1 = section.isList("materials");
        boolean m2 = section.isList("material");
        if (m1 || m2) {
            List<String> mats = (m1) ? section.getStringList("materials") : section.getStringList("material");
            return mats.stream().map(mat -> (IBuilder) new IAItemBuilder(mat, section, player)).toList();
        }
        boolean t1 = section.isList("types");
        boolean t2 = section.isList("type");
        if (t1 || t2) {
            List<String> mats = (t1) ? section.getStringList("types") : section.getStringList("type");
            return mats.stream().map(mat -> (IBuilder) new IAItemBuilder(mat, section, player)).toList();
        }

        // Method2: Default to single item logic
        return Collections.singletonList(new IAItemBuilder(section, player));
    }
}
