package com.kamikazejam.kamicommon.menu;

import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.access.paginated.IPageIconsAccess;
import com.kamikazejam.kamicommon.menu.api.icons.access.paginated.PageIconsAccess;
import com.kamikazejam.kamicommon.menu.api.icons.slots.StaticIconSlot;
import com.kamikazejam.kamicommon.menu.api.struct.MenuEvents;
import com.kamikazejam.kamicommon.menu.api.struct.icons.PrioritizedMenuIconMap;
import com.kamikazejam.kamicommon.menu.api.struct.paginated.PaginatedMenuOptions;
import com.kamikazejam.kamicommon.menu.api.struct.paginated.Pagination;
import com.kamikazejam.kamicommon.menu.api.struct.paginated.layout.PaginationLayout;
import com.kamikazejam.kamicommon.menu.api.struct.paginated.title.AbstractPaginatedMenuTitle;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSizeRows;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSizeType;
import com.kamikazejam.kamicommon.util.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This Menu class focuses on providing an easy way of creating a menu with multiple pages. This menu allows you to
 * provide all the items you want to display, and it will automatically paginate them for you.<br>
 * Pagination configuration is available by creating your own {@link PaginationLayout} or by modifying a pre-built one.
 */
@Getter
@Accessors(chain = true)
@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class PaginatedMenu extends AbstractMenu<PaginatedMenu> {
    private static final @NotNull String nextIconId = "paginated_menu_next";
    private static final @NotNull String prevIconId = "paginated_menu_prev";
    private static final @NotNull String nextInactiveIconId = "paginated_menu_next_inactive";
    private static final @NotNull String prevInactiveIconId = "paginated_menu_prev_inactive";

    // Fields
    //  Uses priorities for ordering (higher = earlier)
    //  Map<IconId, IconWrappedWithPriority>
    private final @NotNull PrioritizedMenuIconMap<PaginatedMenu> pagedIcons = new PrioritizedMenuIconMap<>();
    @Getter(AccessLevel.NONE)
    private int pageIndex; // 0-indexed

    // Internal Data

    // Constructor (Deep Copying from Builder)
    private PaginatedMenu(@NotNull Builder builder, @NotNull Player player) {
        super(builder, player);
        builder.pagedIcons.values().forEach((icon) -> this.pagedIcons.add(icon.copy()));
        this.pageIndex = 0;
    }

    @Override
    public @NotNull PaginatedMenuOptions getOptions() {
        return (PaginatedMenuOptions) super.getOptions();
    }

    @Override
    public @Nullable InventoryView open() {
        return this.open(0);
    }

    /**
     * @param p The page index to open (0-indexed)
     */
    public @Nullable InventoryView open(int p) {
        this.pageIndex = p;
        final MenuSize size = this.getMenuSize();
        final PaginationLayout layout = this.getOptions().getLayout();

        // Fetch the slots per page
        Collection<Integer> pageSlots = layout.getSlots(size);
        if (pageSlots.isEmpty()) {
            throw new IllegalStateException("[PaginatedMenu] No slots were found for the given layout and size. Cannot open menu!");
        }
        pageSlots.removeIf(s -> s < 0 || s >= size.getNumberOfSlots()); // Remove invalid slots

        // We need ascending order because the priority is calculated based on the order of insertion
        // So the lower the priority, the earlier it was inserted, and the earlier we want it to be displayed
        List<MenuIcon<PaginatedMenu>> icons = pagedIcons.getAllByAscendingPriority(true);
        Pagination<MenuIcon<PaginatedMenu>> pagination = new Pagination<>(pageSlots.size(), icons); // use pageSlots as per-page size

        // Clamp the page index
        int totalPages = pagination.totalPages(); // 1-indexed
        if (this.pageIndex < 0) {this.pageIndex = 0;}
        if (this.pageIndex >= totalPages) {this.pageIndex = totalPages - 1;}

        // Evaluate the title
        AbstractPaginatedMenuTitle menuTitle = getOptions().getTitleFormat();
        super.setTitle(menuTitle.getMenuTitle(this, (this.pageIndex + 1), totalPages));

        // Add the Control Icons
        modifyIcons((access) -> {
            access.removeMenuIcon(nextIconId);
            access.removeMenuIcon(prevIconId);

            // Add the Next Icon
            if ((this.pageIndex + 1 < totalPages)) {
                // There is a next page, place the next icon if enabled
                @Nullable MenuIcon<PaginatedMenu> nextIcon = this.getOptions().getNextPageIcon();
                if (nextIcon != null && nextIcon.isEnabled()) {
                    access.setMenuIcon(nextIcon.setId(nextIconId), layout.getNextIconSlot(size));
                    access.setMenuClick(nextIconId, (data) -> {
                        if (this.pageIndex + 1 < totalPages) {
                            this.pageIndex++;
                        }
                        this.getEvents().getIgnoreNextInventoryCloseEvent().set(true);
                        this.open(this.pageIndex);
                    });
                }
            } else {
                // There is no next page, place the inactive icon if enabled
                @Nullable MenuIcon<PaginatedMenu> nextInactiveIcon = this.getOptions().getNextPageInactiveIcon();
                if (nextInactiveIcon != null && nextInactiveIcon.isEnabled()) {
                    access.setMenuIcon(nextInactiveIcon.setId(nextInactiveIconId), layout.getNextIconSlot(size));
                }
            }

            // Add the Prev Icon
            if (this.pageIndex > 0) {
                // There is a previous page, place the prev icon if enabled
                @Nullable MenuIcon<PaginatedMenu> prevIcon = this.getOptions().getPrevPageIcon();
                if (prevIcon != null && prevIcon.isEnabled()) {
                    access.setMenuIcon(prevIcon.setId(prevIconId), layout.getPrevIconSlot(size));
                    access.setMenuClick(prevIconId, (data) -> {
                        if (this.pageIndex > 0) {
                            this.pageIndex--;
                        }
                        this.getEvents().getIgnoreNextInventoryCloseEvent().set(true);
                        this.open(this.pageIndex);
                    });
                }
            } else {
                // There is no previous page, place the inactive icon if enabled
                @Nullable MenuIcon<PaginatedMenu> prevInactiveIcon = this.getOptions().getPrevPageInactiveIcon();
                if (prevInactiveIcon != null && prevInactiveIcon.isEnabled()) {
                    access.setMenuIcon(prevInactiveIcon.setId(prevInactiveIconId), layout.getPrevIconSlot(size));
                }
            }
        });

        // Remove the IDs of all paged icons, since we don't want old icons to show
        // Without this step, if the last page has less than the full layout slots, old icons from the previous page will show at the end
        modifyIcons((access) -> icons.forEach((icon) -> access.removeMenuIcon(icon.getId())));

        // Add all page items
        if (pagination.pageExist(this.pageIndex)) {
            List<Integer> placeableSlots = new ArrayList<>(pageSlots);
            List<MenuIcon<PaginatedMenu>> pageIcons = pagination.getPage(this.pageIndex);
            for (int i = 0; i < placeableSlots.size(); i++) {
                if (i >= pageIcons.size()) {break;}

                int slot = placeableSlots.get(i);
                MenuIcon<PaginatedMenu> menuIcon = pageIcons.get(i);

                // Place the icon in the slot
                modifyIcons((access) -> access.setMenuIcon(menuIcon, new StaticIconSlot(slot)));
            }
        }

        // Make sure the inventory is empty / deleted so the title & size are used properly
        super.deleteInventory();
        return super.open();
    }

    @Override
    protected void placeFiller(Map<Integer, @Nullable ItemStack> newMenuState, Set<Integer> slots, int tick) {
        Set<Integer> newSlots = new HashSet<>(slots);
        if (!this.getOptions().isFillerFillsEmptyPageIconSlots()) {
            newSlots.removeAll(this.getOptions().getLayout().getSlots(this.getMenuSize()));
        }

        super.placeFiller(newMenuState, newSlots, tick);
    }

    // ------------------------------------------------------------ //
    //                     PaginatedMenu Methods                    //
    // ------------------------------------------------------------ //

    /**
     * @return The current page (0-indexed)
     */
    public int getCurrentPage() {
        return pageIndex;
    }

    public @NotNull PaginatedMenu modifyPageIcons(@NotNull Consumer<IPageIconsAccess<PaginatedMenu>> consumer) {
        consumer.accept(new PageIconsAccess<>(this.pagedIcons));
        return this;
    }

    // ------------------------------------------------------------ //
    //                        Builder Pattern                       //
    // ------------------------------------------------------------ //
    public static final class Builder extends AbstractMenuBuilder<PaginatedMenu, Builder> {
        // Pagination Specific Fields
        private final @NotNull PrioritizedMenuIconMap<PaginatedMenu> pagedIcons = new PrioritizedMenuIconMap<>();

        public Builder(@NotNull PaginationLayout layout, @NotNull MenuSize size) {
            super(size, new MenuEvents<>(), new PaginatedMenuOptions(layout));
        }

        public Builder(@NotNull PaginationLayout layout, int rows) {
            this(layout, new MenuSizeRows(rows));
        }

        public Builder(@NotNull PaginationLayout layout, @NotNull InventoryType type) {
            this(layout, new MenuSizeType(type));
        }

        public @NotNull Builder paginationOptions(PaginatedMenuOptions.@NotNull PaginatedMenuOptionsModification modification) {
            Preconditions.checkNotNull(modification, "Modification must not be null.");
            modification.modify((PaginatedMenuOptions) this.options);
            return this;
        }

        public @NotNull Builder modifyPageIcons(@NotNull Consumer<IPageIconsAccess<PaginatedMenu>> consumer) {
            consumer.accept(new PageIconsAccess<>(this.pagedIcons));
            return this;
        }

        @CheckReturnValue
        public @NotNull PaginatedMenu build(@NotNull Player player) {
            return new PaginatedMenu(this, player);
        }
    }
}
