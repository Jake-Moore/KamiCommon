package com.kamikazejam.kamicommon.menu.api.struct.paginated.layout;

import com.kamikazejam.kamicommon.SpigotUtilsSource;
import com.kamikazejam.kamicommon.menu.PaginatedMenu;
import com.kamikazejam.kamicommon.menu.api.icons.slots.IconSlot;
import com.kamikazejam.kamicommon.menu.api.icons.slots.PositionSlot;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a layout for a {@link PaginatedMenu} where the available slots for page icons are defined
 * as the grid between two positions ({@link PositionSlot})
 */
@Getter
@SuppressWarnings("unused")
public class GridPaginationLayout implements PaginationLayout {
    private @NotNull PositionSlot min;
    private @NotNull PositionSlot max;
    @Setter
    private @NotNull IconSlot prevIconSlot;
    @Setter
    private @NotNull IconSlot nextIconSlot;
    public GridPaginationLayout(@NotNull PositionSlot a, @NotNull PositionSlot b, @NotNull IconSlot prevIconSlot, @NotNull IconSlot nextIconSlot) {
        this.min = new PositionSlot(Math.min(a.getRow(), b.getRow()), Math.min(a.getCol(), b.getCol()));
        this.max = new PositionSlot(Math.max(a.getRow(), b.getRow()), Math.max(a.getCol(), b.getCol()));
        this.prevIconSlot = prevIconSlot;
        this.nextIconSlot = nextIconSlot;
    }
    // Copy Constructor
    private GridPaginationLayout(@NotNull GridPaginationLayout other) {
        this.min = (PositionSlot) other.min.copy();
        this.max = (PositionSlot) other.max.copy();
        this.prevIconSlot = other.prevIconSlot.copy();
        this.nextIconSlot = other.nextIconSlot.copy();
    }

    public void update(@NotNull PositionSlot a, @NotNull PositionSlot b) {
        this.min = new PositionSlot(Math.min(a.getRow(), b.getRow()), Math.min(a.getCol(), b.getCol()));
        this.max = new PositionSlot(Math.max(a.getRow(), b.getRow()), Math.max(a.getCol(), b.getCol()));
    }

    @Override
    public @NotNull Collection<Integer> getSlots(@NotNull MenuSize size) {
        List<Integer> slots = new ArrayList<>(); // List for ordering
        for (int row = min.getRow(); row <= max.getRow(); row++) {
            for (int col = min.getCol(); col <= max.getCol(); col++) {
                try {
                    slots.add(size.mapPositionToSlot(row, col));
                }catch (Exception e) {
                    SpigotUtilsSource.get().getColorLogger().warn("[GridPaginationLayout] Error while trying to map position (" + row + "," + col + ") to slot: " + e.getMessage());
                }
            }
        }
        return slots;
    }

    @Override
    public @NotNull IconSlot getNextIconSlot(@NotNull MenuSize size) {
        return this.nextIconSlot;
    }

    @Override
    public @NotNull IconSlot getPrevIconSlot(@NotNull MenuSize size) {
        return this.prevIconSlot;
    }

    @Override
    public @NotNull PaginationLayout copy() {
        return new GridPaginationLayout(this);
    }
}
