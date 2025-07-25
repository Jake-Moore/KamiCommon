package com.kamikazejam.kamicommon.menu.api.struct.paginated.layout;

import com.kamikazejam.kamicommon.menu.PaginatedMenu;
import com.kamikazejam.kamicommon.menu.api.icons.slots.IconSlot;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Represents the layout of a {@link PaginatedMenu}
 */
public interface PaginationLayout {
    /**
     * Get the slots of the menu that are used for the pages. Will use the ordering of the collection to place slots.<br>
     * Any slots that do not fall within the confines of the menu will be ignored.
     * @param size The {@link MenuSize} for generating an appropriate range of slots.
     * @return The slots of the menu to place page items into.
     */
    @NotNull
    Collection<Integer> getSlots(@NotNull MenuSize size);

    /**
     * Get the slot that the previous icon (if needed) will be placed into.<br>
     * If this slot is invalid (outside the menu), the icon will not be placed.
     */
    @NotNull
    IconSlot getPrevIconSlot(@NotNull MenuSize size);

    /**
     * Get the slot that the next icon (if needed) will be placed into.<br>
     * If this slot is invalid (outside the menu), the icon will not be placed.
     */
    @NotNull
    IconSlot getNextIconSlot(@NotNull MenuSize size);

    @NotNull
    PaginationLayout copy();
}
