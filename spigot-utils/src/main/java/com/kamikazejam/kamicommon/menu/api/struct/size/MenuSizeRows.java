package com.kamikazejam.kamicommon.menu.api.struct.size;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

@Getter
public final class MenuSizeRows implements MenuSize {
    private final int rows;

    public MenuSizeRows(int rows) {
        if (rows <= 0 || rows > 6) throw new IllegalArgumentException("Rows must be between 1 and 6.");
        this.rows = rows;
    }

    @Override
    public @NotNull Inventory createInventory(@NotNull InventoryHolder holder, @NotNull String title) {
        return Bukkit.createInventory(holder, rows * 9, title);
    }

    @Override
    public int getSlotInLastRow(int index) {
        // Require the index to be between 0 and 8, otherwise return -1 so the icon doesn't get placed.
        if (index < 0 || index > 8) {return -1;}
        return (rows * 9) - (9 - index);
    }

    @Override
    public @NotNull MenuSize copy() {
        return new MenuSizeRows(rows);
    }

    @Override
    public int getNumberOfSlots() {
        return rows * 9;
    }

    @Override
    public int mapPositionToSlot(int row, int col) throws IllegalStateException {
        return mapPositionToSlot(row, col, rows);
    }

    // Public for use in MenuSizeType since some InventoryTypes use 9-slot rows.
    public static int mapPositionToSlot(int row, int col, int rows) throws IllegalArgumentException {
        if (row < 1 || row > rows || col < 1 || col > 9) {
            throw new IllegalArgumentException("Cannot map position to slot for row=" + row + ", col=" + col + " in a " + rows + " row menu.");
        }
        // Fun maths that maps our 2D position to a slot number, since our inventory in this class is always a 9 by X grid.
        return (row - 1) * 9 + (col - 1);
    }
}
