package com.kamikazejam.kamicommon.gui.page;

import com.kamikazejam.kamicommon.gui.KamiMenu;
import com.kamikazejam.kamicommon.gui.items.MenuItem;
import com.kamikazejam.kamicommon.gui.items.slots.StaticItemSlot;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.xseries.XMaterial;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "SameReturnValue"})
public abstract class PageBuilder {

    @Getter public int currentPage;
    @Getter private @Nullable KamiMenu menu;
    private @Nullable Pagination<MenuItem> pagination = null;
    @Setter private List<Integer> slotsOverride = null;

    public PageBuilder() {
        // We will calculate the page size based on available rows (lazily)
    }

    public PageBuilder(@NotNull List<Integer> slots) {
        this.pagination = new Pagination<>(slots.size(), this.getPagination());
        slotsOverride = slots;
    }

    public PageBuilder(int[] slots) {
        this.pagination = new Pagination<>(slots.length, this.getPagination());
        slotsOverride = new ArrayList<>();
        for (int slot : slots) {
            slotsOverride.add(slot);
        }
    }

    // List naturally sorted in increasing numerical order
    @NotNull
    public List<Integer> getPlacedSlots(@NotNull KamiMenu menu) {
        // If we have an override, then implicitly our Pagination is using this size too
        // We can just return the override
        if (slotsOverride != null) { return slotsOverride; }

        // We have no override, meaning our Pagination was created with a pageSize of 21
        // We can create 21 default slot locations and return them
        int rows = (int) Math.ceil(menu.getSize() / 9.0);
        List<Integer> slots = new ArrayList<>();
        for (int i = 1; i <= (rows-3); i++) {
            slots.add(9 * i + 1);
            slots.add(9 * i + 2);
            slots.add(9 * i + 3);
            slots.add(9 * i + 4);
            slots.add(9 * i + 5);
            slots.add(9 * i + 6);
            slots.add(9 * i + 7);
        }
        return slots;
    }

    public Pagination<MenuItem> getPageItems() {
        return this.pagination;
    }

    // Overridable (currentPage is 1 indexed, maxPages is 1 indexed)
    public @Nonnull String getMenuName(int currentPage, int maxPages) {
        return getMenuName() + (maxPages > 1 ? " (Page " + (currentPage) + "/" + maxPages + ")" : "");
    }

    public abstract String getMenuName();

    public abstract @NotNull Collection<MenuItem> getItems();

    public IBuilder getNextPageIcon() {
        return new ItemBuilder(XMaterial.ARROW).setName("&a&lNext Page &a▶");
    }

    public IBuilder getPrevPageIcon() {
        return new ItemBuilder(XMaterial.ARROW).setName("&a◀ &a&lPrevious Page");
    }

    public int getFixedSize() {
        return -1;
    }

    public int getLinesFilled(int page) {
        int totalItems = this.getPagination().pageSize();
        int totalItemsPage = this.getPagination().size();
        return Math.min(6, (int) (3 + Math.ceil(totalItemsPage - totalItems * page > totalItems ? 3 : (totalItemsPage - totalItems * page) / 7.0)));
    }

    public int getPreviousIconSlot() {
        return Objects.requireNonNull(menu).getInventory().getSize() - 8;
    }

    public int getNextIconSlot() {
        return Objects.requireNonNull(menu).getInventory().getSize() - 2;
    }

    public int getRows(int page) {
        int rows = getFixedSize() == -1 ? getLinesFilled(page) : getFixedSize();
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("Invalid rows: " + rows);
        }
        return rows;
    }

    @NotNull
    public KamiMenu createBlankMenu(@NotNull String title, int page) {
        return new KamiMenu(title, getRows(page), this);
    }

    @Nullable
    public IBuilder getFillerItem() {
        return null;
    }

    @Nullable
    public MenuItem getFillerIcon() {
        IBuilder fillerItem = getFillerItem();
        if (fillerItem == null) { return null; }
        return new MenuItem(true, fillerItem, -1);
    }

    public void openMenu(Player player, int page) {
        // createMenu handles the currentPage variable
        createMenu(player, page).openMenu(player);
    }

    @NotNull
    private Pagination<MenuItem> getPagination() {
        if (pagination != null) {
            return pagination;
        }
        int rows = (int) Math.ceil(Objects.requireNonNull(this.menu).getSize() / 9.0);
        // By default, we exclude the top row, bottom row, and second to last row for nice formatting
        int rowsToPlace = Math.max(1, rows - 3);
        // Use rowsToPlace * 7 (7 items per row) to determine the pageSize
        return pagination = new Pagination<>(rowsToPlace * 7, this.getItems());
    }

    // page 0 indexed
    public KamiMenu createMenu(Player player, int page) {
        // Create our menu (sample title, our real one gets replaced once we know the total pages)
        this.menu = this.createBlankMenu(getMenuName(), page);

        // Lazy-load the pagination, now that we have a menu with a size
        Pagination<MenuItem> pagination = this.getPagination();

        // Update the title with the pagination data
        this.menu.setInvName(StringUtil.t(getMenuName(page + 1, pagination.totalPages())));

        // Add previous icon
        if (page > 0) {
            menu.addMenuItem(getPrevPageIcon(), getPreviousIconSlot()).setMenuClick((plr, type) -> {
                menu.getIgnoredClose().add(plr.getName());
                openMenu(plr, (page - 1));
            });
        }

        // Add next icon
        if (pagination.pageExist(page + 1)) {
            menu.addMenuItem(getNextPageIcon(), getNextIconSlot()).setMenuClick((plr, type) -> {
                menu.getIgnoredClose().add(plr.getName());
                openMenu(plr, (page + 1));
            });
        }

        // Add other icons (lower priority than page items)
        for (MenuItem item : this.supplyOtherIcons()) {
            if (item == null || !item.isEnabled()) { continue; }
            menu.addMenuItem(item);
        }

        // Add all page items
        if (pagination.pageExist(page)) {
            List<Integer> placeableSlots = getPlacedSlots(menu);

            for (MenuItem menuItem : pagination.getPage(page)) {
                if (menuItem == null) { continue; }
                int s = firstEmpty(placeableSlots, menu);
                if (s == -1) { break; }

                // Add the page item to the menu
                menuItem.setItemSlot(new StaticItemSlot(s));
                menu.addMenuItem(menuItem);
                placeableSlots.remove(Integer.valueOf(s)); // Mark it as filled, so firstEmpty moves to the next slot
            }
        }

        // Place all the items we just added
        menu.placeItems();

        // Fill empty slots
        tryFill();

        // We make some assumptions, like that this menu object belongs to 1 player
        menu.whenOpened((plr, v) -> this.currentPage = page);
        return menu;
    }

    private void tryFill() {
        if (menu == null) { return; }

        // Filler items
        MenuItem fillerItem = getFillerIcon();
        if (fillerItem != null && fillerItem.isEnabled()) {
            @Nullable IBuilder builder = fillerItem.getNextBuilder();
            if (builder == null) { return; }

            menu.fill(builder);
        }
    }

    public Collection<MenuItem> supplyOtherIcons() {
        return new ArrayList<>();
    }

    private int firstEmpty(@NotNull List<Integer> placeableSlots, @NotNull KamiMenu menu) {
        for (int i : placeableSlots) {
            if (i >= menu.getSize()) {
                return -1;
            }
            if (menu.getInventory().getItem(i) == null) {
                return i;
            }
        }
        return -1;
    }
}
