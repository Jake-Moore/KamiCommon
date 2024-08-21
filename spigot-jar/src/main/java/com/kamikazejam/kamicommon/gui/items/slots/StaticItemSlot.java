package com.kamikazejam.kamicommon.gui.items.slots;

import com.kamikazejam.kamicommon.gui.KamiMenu;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StaticItemSlot implements ItemSlot {
    private final @NotNull List<Integer> slots;
    public StaticItemSlot(@NotNull List<Integer> slots) {
        this.slots = slots;
    }
    public StaticItemSlot(int slot) {
        this.slots = List.of(slot);
    }

    @Override
    public List<Integer> get(@NotNull KamiMenu menu) {
        return slots;
    }
}
