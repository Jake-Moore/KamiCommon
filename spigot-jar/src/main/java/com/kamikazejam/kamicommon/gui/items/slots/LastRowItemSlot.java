package com.kamikazejam.kamicommon.gui.items.slots;

import com.kamikazejam.kamicommon.gui.KamiMenu;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class LastRowItemSlot implements ItemSlot {
    private final int slotInLastRow;
    public LastRowItemSlot(int slotInLastRow) {
        this.slotInLastRow = slotInLastRow;
    }

    @Override
    public Set<Integer> get(@NotNull KamiMenu menu) {
        int slot = menu.getSize() - (9 - slotInLastRow);
        return Set.of(slot);
    }

    @Override
    public @NotNull ItemSlot copy() {
        return new LastRowItemSlot(slotInLastRow);
    }
}
