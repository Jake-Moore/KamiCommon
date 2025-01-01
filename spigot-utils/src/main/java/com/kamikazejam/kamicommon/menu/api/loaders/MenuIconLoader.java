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

        // Load the IconSlot
        @Nullable IconSlot iconSlot = loadSlots(section);

        // Create the MenuIcon
        MenuIcon icon = new MenuIcon(enabled, iconSlot, iBuilders);

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

    @Nullable
    private static IconSlot loadSlots(ConfigurationSection section) {

        // Try to load a relative slot (high priority).
        // If set to -1, ignore this key
        if (section.isInt("slotInLastRow")) {
            int slotInLast = section.getInt("slotInLastRow");
            if (slotInLast >= 0) {
                return new LastRowIconSlot(slotInLast);
            }
        }

        // Load Static Slots (if found).
        // If a slot is -1, ignore it
        List<Integer> slots = new ArrayList<>();
        if (section.isInt("slot")) {
            int s = section.getInt("slot");
            if (s >= 0) {
                slots.add(s);
            }
        }else if (section.isList("slot")) {
            List<Integer> s = section.getIntegerList("slot");
            s.forEach(slot -> {
                if (slot < 0) { return; }
                slots.add(slot);
            });
        }else if (section.isList("slots")) {
            List<Integer> s = section.getIntegerList("slots");
            s.forEach(slot -> {
                if (slot < 0) { return; }
                slots.add(slot);
            });
        }else if (section.isInt("slots")) {
            int s = section.getInt("slots");
            if (s >= 0) {
                slots.add(s);
            }
        }

        // Return null if we didn't find any valid slots
        return (slots.isEmpty()) ? null : new StaticIconSlot(slots);
    }
}
