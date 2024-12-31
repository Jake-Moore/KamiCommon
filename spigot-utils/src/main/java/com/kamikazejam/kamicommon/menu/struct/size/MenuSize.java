package com.kamikazejam.kamicommon.menu.struct.size;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the size of a menu.<br>
 * Can either be a {@link MenuSizeRows}, or {@link MenuSizeType}.
 */
public sealed interface MenuSize permits MenuSizeRows, MenuSizeType {
    /**
     * Creates a new {@link Inventory} with the given {@link InventoryHolder} and title.<br>
     * Uses this {@link MenuSize} to determine the size of the inventory.
     */
    @NotNull Inventory createInventory(@NotNull InventoryHolder holder, @NotNull String title);

    /**
     * Returns the slot number for the slot in the last row with the given index in that row.
     */
    int getSlotInLastRow(int index);

    /**
     * Deep copies this {@link MenuSize} into an identical object clone.
     */
    @NotNull MenuSize copy();
}

