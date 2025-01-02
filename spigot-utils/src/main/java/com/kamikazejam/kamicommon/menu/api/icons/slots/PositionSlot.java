package com.kamikazejam.kamicommon.menu.api.icons.slots;

import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter
@SuppressWarnings("unused")
public class PositionSlot implements IconSlot {
    private final int row;
    private final int col;

    /**
     * Create a new PositionSlot, from a row/col format
     * @param row The row (1 indexed)
     * @param col The column (1 indexed)
     */
    public PositionSlot(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public Set<Integer> get(@NotNull MenuSize size) {
        return Set.of(size.mapPositionToSlot(row, col));
    }

    @Override
    public @NotNull IconSlot copy() {
        return new PositionSlot(row, col);
    }
}
