package com.kamikazejam.kamicommon.menu.api.struct.paginated.layout;

import com.kamikazejam.kamicommon.menu.PaginatedMenu;
import com.kamikazejam.kamicommon.menu.api.icons.slots.IconSlot;
import com.kamikazejam.kamicommon.menu.api.icons.slots.LastRowIconSlot;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * Represents the simplest layout for a {@link PaginatedMenu}.<br>
 * This layout is defined by:
 * <ul>
 *     <li> A 1 block gap (border) at the top of the menu where no items are placed.</li>
 *     <li> A 1 block gap (border) at the left and right of the menu where no items are placed.</li>
 *     <li> The last row is reserved for the pagination controls.</li>
 *     <li> The second to last row is also a gap (border) row, such that the remaining rows in between are used for the page items.</li>
 * </ul>
 * NOTE: This layout only supports menu sizes that have at least 4 rows. (and only works properly on 9-column menus)
 */
@SuppressWarnings("unused")
public class SimplePaginationLayout implements PaginationLayout {
    public SimplePaginationLayout() {}

    @Override
    public @NotNull Collection<Integer> getSlots(@NotNull MenuSize size) {
        if (size.getNumberOfSlots() < 36 || size.getNumberOfSlots() % 9 != 0) {
            throw new IllegalArgumentException("[SimplePaginationLayout] Invalid menu size: " + size.getNumberOfSlots() + " slots. Must be at least 36 slots and divisible by 9.");
        }
        int rows = size.getNumberOfSlots() / 9;

        List<Integer> slots = new java.util.ArrayList<>(); // List for ordering
        for (int row = 2; row <= rows - 2; row++) {
            for (int col = 2; col <= 8; col++) {
                slots.add(size.mapPositionToSlot(row, col));
            }
        }
        return slots;
    }

    @Override
    public @NotNull IconSlot getNextIconSlot(@NotNull MenuSize size) {
        return new LastRowIconSlot(7);
    }

    @Override
    public @NotNull IconSlot getPrevIconSlot(@NotNull MenuSize size) {
        return new LastRowIconSlot(1);
    }

    @Override
    public @NotNull PaginationLayout copy() {
        return new SimplePaginationLayout();
    }
}
