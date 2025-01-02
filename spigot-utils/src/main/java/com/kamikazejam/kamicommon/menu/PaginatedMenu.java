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
import com.kamikazejam.kamicommon.menu.api.struct.paginated.title.DefaultPaginatedMenuTitle;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * This Menu class focuses on providing an easy way of creating a menu with multiple pages. This menu allows you to
 * provide all the items you want to display, and it will automatically paginate them for you.<br>
 * Pagination configuration is available by creating your own {@link PaginationLayout} or by modifying a pre-built one.
 */
@Getter
@Accessors(chain = true)
@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class PaginatedMenu extends SimpleMenu<PaginatedMenu> {
    private static final String nextIconId = "paginated_menu_next";
    private static final String prevIconId = "paginated_menu_prev";

    // Fields
    //  Uses priorities for ordering (higher = earlier)
    //  Map<IconId, IconWrappedWithPriority>
    private final @NotNull PrioritizedMenuIconMap pagedIcons = new PrioritizedMenuIconMap();
    @Getter(AccessLevel.NONE)
    private int pageIndex; // 0-indexed

    // Internal Data

    // Constructor (Deep Copying from Builder)
    private PaginatedMenu(@NotNull Builder<?> builder, @NotNull Player player) {
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
        List<MenuIcon> icons = pagedIcons.getAllByAscendingPriority(true);
        Pagination<MenuIcon> pagination = new Pagination<>(pageSlots.size(), icons); // use pageSlots as per-page size

        // Clamp the page index
        int totalPages = pagination.totalPages(); // 1-indexed
        if (this.pageIndex < 0) { this.pageIndex = 0; }
        if (this.pageIndex >= totalPages) { this.pageIndex = totalPages - 1; }

        // Evaluate the title
        DefaultPaginatedMenuTitle menuTitle = getOptions().getTitleFormat();
        super.setTitle(menuTitle.getMenuTitle(this, (this.pageIndex+1), totalPages));

        // Add the Control Icons
        modifyIcons((access) -> {
            access.removeMenuIcon(nextIconId);
            access.removeMenuIcon(prevIconId);
            // Add the Next Icon
            @Nullable MenuIcon nextIcon = this.getOptions().getNextPageIcon();
            if ((this.pageIndex + 1 < totalPages) && nextIcon != null && nextIcon.isEnabled()) {
                access.setMenuIcon(nextIcon.setId(nextIconId), layout.getNextIconSlot(size));
                access.setMenuClick(nextIconId, (plr, c) -> {
                    if (this.pageIndex + 1 < totalPages) {
                        this.pageIndex++;
                    }
                    this.getEvents().getIgnoreNextInventoryCloseEvent().set(true);
                    this.open(this.pageIndex);
                });
            }
            // Add the Prev Icon
            @Nullable MenuIcon prevIcon = this.getOptions().getPrevPageIcon();
            if (this.pageIndex > 0 && prevIcon != null && prevIcon.isEnabled()) {
                access.setMenuIcon(prevIcon.setId(prevIconId), layout.getPrevIconSlot(size));
                access.setMenuClick(prevIconId, (plr, c) -> {
                    if (this.pageIndex > 0) {
                        this.pageIndex--;
                    }
                    this.getEvents().getIgnoreNextInventoryCloseEvent().set(true);
                    this.open(this.pageIndex);
                });
            }
        });

        // Remove the IDs of all paged icons, since we don't want old icons to show
        // Without this step, if the last page has less than the full layout slots, old icons from the previous page will show at the end
        modifyIcons((access) -> icons.forEach((icon) -> access.removeMenuIcon(icon.getId())));

        // Add all page items
        if (pagination.pageExist(this.pageIndex)) {
            List<Integer> placeableSlots = new ArrayList<>(pageSlots);
            List<MenuIcon> pageIcons = pagination.getPage(this.pageIndex);
            for (int i = 0; i < placeableSlots.size(); i++) {
                if (i >= pageIcons.size()) { break; }

                int slot = placeableSlots.get(i);
                MenuIcon menuIcon = pageIcons.get(i);

                // Place the icon in the slot
                modifyIcons((access) -> access.setMenuIcon(menuIcon, new StaticIconSlot(slot)));
            }
        }

        // Make sure the inventory is empty / deleted so the title & size are used properly
        super.deleteInventory();
        return super.open();
    }

    @Override
    protected void placeFiller(Map<String, Boolean> needsUpdateMap, Map<String, ItemStack> itemStackMap, Set<Integer> slots, int tick) {
        // If we don't want the filler to fill empty page slots, remove all page slots from the filler's slots
        if (!this.getOptions().isFillerFillsEmptyPageIconSlots()) {
            slots.removeAll(this.getOptions().getLayout().getSlots(this.getMenuSize()));
        }

        super.placeFiller(needsUpdateMap, itemStackMap, slots, tick);
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

    public @NotNull PaginatedMenu modifyPageIcons(@NotNull Consumer<IPageIconsAccess> consumer) {
        consumer.accept(new PageIconsAccess(this.pagedIcons));
        return this;
    }

    // ------------------------------------------------------------ //
    //                        Builder Pattern                       //
    // ------------------------------------------------------------ //
    @SuppressWarnings("unchecked")
    public static final class Builder<T extends Builder<T>> extends SimpleMenu.Builder<T> {
        // Pagination Specific Fields
        private final @NotNull PrioritizedMenuIconMap pagedIcons = new PrioritizedMenuIconMap();

        public Builder(@NotNull PaginationLayout layout, @NotNull MenuSize size) {
            super(size, new MenuEvents(), new PaginatedMenuOptions(layout));
        }
        public Builder(@NotNull PaginationLayout layout, int rows) {
            this(layout, new MenuSizeRows(rows));
        }
        public Builder(@NotNull PaginationLayout layout, @NotNull InventoryType type) {
            this(layout, new MenuSizeType(type));
        }

        public @NotNull T paginationOptions(PaginatedMenuOptions.@NotNull PaginatedMenuOptionsModification modification) {
            Preconditions.checkNotNull(modification, "Modification must not be null.");
            modification.modify((PaginatedMenuOptions) this.options);
            return (T) this;
        }

        public @NotNull T modifyPageIcons(@NotNull Consumer<IPageIconsAccess> consumer) {
            consumer.accept(new PageIconsAccess(this.pagedIcons));
            return (T) this;
        }

        @Override
        public @NotNull PaginatedMenu build(@NotNull Player player) {
            return new PaginatedMenu(this, player);
        }
    }
}
