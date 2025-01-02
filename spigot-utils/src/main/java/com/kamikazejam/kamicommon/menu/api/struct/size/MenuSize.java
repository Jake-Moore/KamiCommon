package com.kamikazejam.kamicommon.menu.api.struct.size;

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

    /**
     * @return the 1-index number of slots in this menu size.
     */
    int getNumberOfSlots();

    /**
     * Maps a position to a slot number. For example position (0, 0) maps to slot 0 in a traditional inventory.
     * @param row The row (top to bottom) of the position. (0-indexed)
     * @param col The column (left to right) of the position. (0-indexed)
     * @throws IllegalArgumentException if the given position is outside the bounds of this MenuSize.
     * @throws IllegalStateException if this method is called on an unsupported MenuSize (including some {@link MenuSizeType}s using non-standard {@link org.bukkit.event.inventory.InventoryType} configurations.)
     * @return The bukkit slot number corresponding to the given position.
     */
    int mapPositionToSlot(int row, int col) throws IllegalArgumentException, IllegalStateException;
}

