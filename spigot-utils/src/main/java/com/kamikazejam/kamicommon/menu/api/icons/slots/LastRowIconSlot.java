package com.kamikazejam.kamicommon.menu.api.icons.slots;

import com.kamikazejam.kamicommon.menu.Menu;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class LastRowIconSlot implements IconSlot {
    private final int slotInLastRow;
    public LastRowIconSlot(int slotInLastRow) {
        this.slotInLastRow = slotInLastRow;
    }

    @Override
    public Set<Integer> get(@NotNull Menu menu) {
        // Use MenuSize since we don't know the shape or form of the menu
        return Set.of(menu.getMenuSize().getSlotInLastRow(slotInLastRow));
    }

    @Override
    public @NotNull IconSlot copy() {
        return new LastRowIconSlot(slotInLastRow);
    }
}
