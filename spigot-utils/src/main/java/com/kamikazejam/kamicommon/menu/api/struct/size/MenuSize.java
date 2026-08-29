package com.kamikazejam.kamicommon.menu.api.struct.size;

import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the size of a menu.<br>
 * Can either be a {@link MenuSizeRows}, or {@link MenuSizeType}.
 * <p>
 * <b>Do not implement this outside KamiCommon.</b> This was a {@code sealed} hierarchy until
 * spigot-utils dropped to Java 8 so that 1.8.x servers could load it; {@code sealed} is Java 17
 * and has no Java 8 spelling. The closed set is still enforced <i>within</i> the library by the
 * {@code verifySealedHierarchies} build task, which fails if an implementation appears that is
 * not on the permitted list (MenuSizeRows, MenuSizeType). Nothing can enforce it in your code, hence this annotation.
 */
@ApiStatus.NonExtendable
public interface MenuSize {
    /**
     * Creates a new {@link Inventory} with the given {@link InventoryHolder} and title.<br>
     * Uses this {@link MenuSize} to determine the size of the inventory.
     */
    @NotNull Inventory createInventory(@NotNull InventoryHolder owner, @NotNull VersionedComponent title);

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
     * Maps a position to a slot number. For example position (1, 1) maps to slot 0 (top-left) in a traditional inventory.
     * @param row The row (top to bottom) of the position. (1-indexed)
     * @param col The column (left to right) of the position. (1-indexed)
     * @throws IllegalArgumentException if the given position is outside the bounds of this MenuSize.
     * @throws IllegalStateException if this method is called on an unsupported MenuSize (including some {@link MenuSizeType}s using non-standard {@link org.bukkit.event.inventory.InventoryType} configurations.)
     * @return The bukkit slot number corresponding to the given position.
     */
    int mapPositionToSlot(int row, int col) throws IllegalArgumentException, IllegalStateException;
}

