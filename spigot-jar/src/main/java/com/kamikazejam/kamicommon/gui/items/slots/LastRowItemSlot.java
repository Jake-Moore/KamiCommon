package com.kamikazejam.kamicommon.gui.items.slots;

import com.kamikazejam.kamicommon.gui.interfaces.Menu;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LastRowItemSlot implements ItemSlot {
    private final int slotInLastRow;
    public LastRowItemSlot(int slotInLastRow) {
        this.slotInLastRow = slotInLastRow;
    }

    @Override
    public List<Integer> get(@NotNull Menu menu) {
        int slot = menu.getSize() - (9 - slotInLastRow);
        return List.of(slot);
    }
}
