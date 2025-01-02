package com.kamikazejam.kamicommon.menu;

import com.kamikazejam.kamicommon.menu.api.struct.MenuEvents;
import com.kamikazejam.kamicommon.menu.api.struct.icons.PrioritizedMenuIconMap;
import com.kamikazejam.kamicommon.menu.api.struct.paginated.PaginatedMenuOptions;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import com.kamikazejam.kamicommon.util.Preconditions;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This Menu class focuses on providing an easy way of creating a menu with multiple pages. This menu allows you to
 * provide all the items you want to display, and it will automatically paginate them for you.<br>
 * Pagination configuration is available (TODO DOCS)
 */
@Getter
@Accessors(chain = true)
@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class PaginatedMenu extends SimpleMenu {
    // Fields
    //  Uses priorities for ordering (higher = earlier)
    //  Map<IconId, IconWrappedWithPriority>
    private final PrioritizedMenuIconMap pagedIcons = new PrioritizedMenuIconMap();
    private int currentPage;

    // Internal Data

    // Constructor (Deep Copying from Builder)
    PaginatedMenu(@NotNull Builder<?> builder, @NotNull Player player) {
        super(builder, player);
        this.currentPage = 0;
    }

    @Override
    public @NotNull PaginatedMenuOptions getOptions() {
        return (PaginatedMenuOptions) super.getOptions();
    }

    @Override
    public @Nullable InventoryView open() {


        // TODO
        // DefaultPaginatedMenuTitle menuTitle = getOptions().getTitleFormat();
        // String title = menuTitle.getMenuTitle(this, currentPage, MAX_PAGES??);

        return super.open();
    }



    // ------------------------------------------------------------ //
    //                        Builder Pattern                       //
    // ------------------------------------------------------------ //
    @SuppressWarnings("unchecked")
    public static final class Builder<T extends Builder<T>> extends SimpleMenu.Builder<T> {
        // Pagination Specific Fields

        public Builder(@NotNull MenuSize size) {
            super(size, new MenuEvents(), new PaginatedMenuOptions());
        }
        public Builder(int rows) {
            super(rows);
        }
        public Builder(@NotNull InventoryType type) {
            super(type);
        }

        public @NotNull T paginationOptions(PaginatedMenuOptions.@NotNull PaginatedMenuOptionsModification modification) {
            Preconditions.checkNotNull(modification, "Modification must not be null.");
            modification.modify((PaginatedMenuOptions) this.options);
            return (T) this;
        }

        @Override
        public @NotNull PaginatedMenu build(@NotNull Player player) {
            return new PaginatedMenu(this, player);
        }
    }
}
