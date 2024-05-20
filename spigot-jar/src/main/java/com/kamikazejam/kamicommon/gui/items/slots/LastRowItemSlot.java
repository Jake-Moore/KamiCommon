package com.kamikazejam.kamicommon.gui.items.slots;

import com.kamikazejam.kamicommon.gui.page.PageBuilder;

import java.util.List;

public class LastRowItemSlot implements ItemSlot {
    private final int slotInLastRow;
    public LastRowItemSlot(int slotInLastRow) {
        this.slotInLastRow = slotInLastRow;
    }

    @Override
    public List<Integer> get(PageBuilder<?> builder) {
        int slot = builder.getMenu().getSize() - (9 - slotInLastRow);
        return List.of(slot);
    }
}
