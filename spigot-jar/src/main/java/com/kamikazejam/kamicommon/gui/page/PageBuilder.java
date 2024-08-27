package com.kamikazejam.kamicommon.gui.page;

import com.kamikazejam.kamicommon.gui.KamiMenu;
import com.kamikazejam.kamicommon.gui.items.MenuItem;
import com.kamikazejam.kamicommon.gui.items.slots.StaticItemSlot;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilder;
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

@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "SameReturnValue"})
public abstract class PageBuilder {

    @Getter public int currentPage;
    @Getter private KamiMenu menu;
    private final Pagination<MenuItem> items;
    @Setter private List<Integer> slotsOverride = null;

    public PageBuilder() {
        // 21 makes a 7x3 grid, which in a 6 row gui allows for an action row on the final row
        this.items = new Pagination<>(21, this.getItems());
    }

    public PageBuilder(@NotNull List<Integer> slots) {
        this.items = new Pagination<>(slots.size(), this.getItems());
        slotsOverride = slots;
    }

    public PageBuilder(int[] slots) {
        this.items = new Pagination<>(slots.length, this.getItems());
        slotsOverride = new ArrayList<>();
        for (int slot : slots) {
            slotsOverride.add(slot);
        }
    }

    // List naturally sorted in increasing numerical order
    public List<Integer> getPlacedSlots(int page) {
        // If we have an override, then implicitly our Pagination is using this size too
        // We can just return the override
        if (slotsOverride != null) { return slotsOverride; }

        // We have no override, meaning our Pagination was created with a pageSize of 21
        // We can create 21 default slot locations and return them
        int rows = getRows(page);
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
        return this.items;
    }

    // Overridable (currentPage is 1 indexed, maxPages is 1 indexed)
    public @Nonnull String getMenuName(int currentPage, int maxPages) {
        return getMenuName() + (maxPages > 1 ? " (Page " + (currentPage) + "/" + maxPages + ")" : "");
    }

    public abstract String getMenuName();

    public abstract Collection<MenuItem> getItems();

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
        int totalItems = this.items.pageSize();
        int totalItemsPage = this.items.size();
        return Math.min(6, (int) (3 + Math.ceil(totalItemsPage - totalItems * page > totalItems ? 3 : (totalItemsPage - totalItems * page) / 7.0)));
    }

    public int getPreviousIconSlot() {
        return menu.getInventory().getSize() - 8;
    }

    public int getNextIconSlot() {
        return menu.getInventory().getSize() - 2;
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

    // page 0 indexed
    public KamiMenu createMenu(Player player, int page) {
        String title = getMenuName(page + 1, this.items.totalPages());
        this.menu = this.createBlankMenu(title, page);

        // Add previous icon
        if (page > 0) {
            menu.addMenuItem(getPrevPageIcon(), getPreviousIconSlot()).setMenuClick((plr, type) -> {
                menu.getIgnoredClose().add(plr.getName());
                openMenu(plr, (page - 1));
            });
        }

        // Add next icon
        if (items.pageExist(page + 1)) {
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
        if (items.pageExist(page)) {
            for (MenuItem menuItem : items.getPage(page)) {
                if (menuItem == null) { continue; }
                int s = firstEmpty(page);
                if (s == -1) { break; }

                // Add the page item to the menu
                menuItem.setItemSlot(new StaticItemSlot(s));
                menu.addMenuItem(menuItem);
            }
        }

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

    private int firstEmpty(int page) {
        for (int i : getPlacedSlots(page)) {
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
