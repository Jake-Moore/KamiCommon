package com.kamikazejam.kamicommon.gui.items.slots;

import com.kamikazejam.kamicommon.gui.KamiMenu;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class StaticItemSlot implements ItemSlot {
    private final @NotNull Set<Integer> slots;
    public StaticItemSlot(@NotNull List<Integer> slots) {
        this.slots = new HashSet<>(slots);
    }
    public StaticItemSlot(int slot) {
        this.slots = Set.of(slot);
    }

    @Override
    public Set<Integer> get(@NotNull KamiMenu menu) {
        return slots;
    }
}
