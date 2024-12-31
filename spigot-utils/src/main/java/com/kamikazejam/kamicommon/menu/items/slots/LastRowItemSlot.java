package com.kamikazejam.kamicommon.menu.items.slots;

import com.kamikazejam.kamicommon.menu.OLD_KAMI_MENU;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class LastRowItemSlot implements ItemSlot {
    private final int slotInLastRow;
    public LastRowItemSlot(int slotInLastRow) {
        this.slotInLastRow = slotInLastRow;
    }

    @Override
    public Set<Integer> get(@NotNull OLD_KAMI_MENU menu) {
        int slot = menu.getSize() - (9 - slotInLastRow);
        return Set.of(slot);
    }

    @Override
    public @NotNull ItemSlot copy() {
        return new LastRowItemSlot(slotInLastRow);
    }
}
