package com.kamikazejam.kamicommon.menu.api.loaders;

import com.kamikazejam.kamicommon.menu.api.icons.slots.IconSlot;
import com.kamikazejam.kamicommon.menu.api.icons.slots.LastRowIconSlot;
import com.kamikazejam.kamicommon.menu.api.icons.slots.StaticIconSlot;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class IconSlotLoader {
    @Nullable
    public static IconSlot load(@NotNull ConfigurationSection section) {

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
        } else if (section.isList("slot")) {
            List<Integer> s = section.getIntegerList("slot");
            s.forEach(slot -> {
                if (slot < 0) {return;}
                slots.add(slot);
            });
        } else if (section.isList("slots")) {
            List<Integer> s = section.getIntegerList("slots");
            s.forEach(slot -> {
                if (slot < 0) {return;}
                slots.add(slot);
            });
        } else if (section.isInt("slots")) {
            int s = section.getInt("slots");
            if (s >= 0) {
                slots.add(s);
            }
        }

        // Return null if we didn't find any valid slots
        return (slots.isEmpty()) ? null : new StaticIconSlot(slots);
    }
}
