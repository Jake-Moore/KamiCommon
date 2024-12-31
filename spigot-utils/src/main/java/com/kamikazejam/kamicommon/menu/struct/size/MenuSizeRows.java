package com.kamikazejam.kamicommon.menu.struct.size;

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
        if (index < 0 || index > 8) { return -1; }
        return (rows * 9) - (9 - index);
    }

    @Override
    public @NotNull MenuSize copy() {
        return new MenuSizeRows(rows);
    }
}
