package com.kamikazejam.kamicommon.gui.loader;

import com.kamikazejam.kamicommon.gui.items.MenuItem;
import com.kamikazejam.kamicommon.gui.items.slots.ItemSlot;
import com.kamikazejam.kamicommon.gui.items.slots.LastRowItemSlot;
import com.kamikazejam.kamicommon.gui.items.slots.StaticItemSlot;
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

public class MenuItemLoader {
    @NotNull
    public static MenuItem load(@NotNull ConfigurationSection section) {
        return load(section, null);
    }

    @NotNull
    public static MenuItem load(@NotNull ConfigurationSection section, @Nullable OfflinePlayer player) {
        boolean enabled = section.getBoolean("enabled", true);

        // Load the IBuilders
        Collection<IBuilder> iBuilders = loadIBuilders(section, player);
        if (section.getBoolean("hideAttributes", true)) { iBuilders.forEach(IBuilder::hideAttributes); }

        // Load the ItemSlot
        @Nullable ItemSlot itemSlot = loadSlots(section);

        // Create the MenuItem
        MenuItem item = new MenuItem(enabled, itemSlot, iBuilders);

        // Apply additional settings
        if (section.isSet("typeCycleTicks")) {
            item.setBuilderRotateTicks(section.getInt("typeCycleTicks"));
        }

        return item;
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
    private static ItemSlot loadSlots(ConfigurationSection section) {

        // Load Static Slots (if found)
        List<Integer> slots = new ArrayList<>();
        if (section.isInt("slot")) {
            slots.add(section.getInt("slot"));
        }else if (section.isList("slot")) {
            slots.addAll(section.getIntegerList("slot"));
        }else if (section.isList("slots")) {
            slots.addAll(section.getIntegerList("slots"));
        }else if (section.isInt("slots")) {
            slots.add(section.getInt("slots"));
        }
        if (!slots.isEmpty()) {
            return new StaticItemSlot(slots);
        }

        // Try to load a relative slot
        if (section.isInt("slotInLastRow")) {
            return new LastRowItemSlot(section.getInt("slotInLastRow"));
        }

        // No slot data found
        return null;
    }
}
