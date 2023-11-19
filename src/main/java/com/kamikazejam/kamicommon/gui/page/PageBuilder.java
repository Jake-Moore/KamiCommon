package com.kamikazejam.kamicommon.gui.page;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.gui.KamiMenu;
import com.kamikazejam.kamicommon.gui.items.KamiMenuItem;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "SameReturnValue"})
public abstract class PageBuilder<T extends Player> {

    private final int[] middleSlots = new int[]{0, 0, 0, 13, 13, 22, 22, 22};

    @Getter public int currentPage;
    private KamiMenu menu;
    private final Pagination<? extends PageItem> items;
    @Setter private List<Integer> slotsOverride = null;

    public PageBuilder() {
        // 21 makes a 7x3 grid, which in a 6 row gui allows for an action row on the final row
        this.items = new Pagination<>(21, this.getItems());
    }

    public PageBuilder(List<Integer> slots) {
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

    public List<Integer> getPlacedSlots(int page) {
        if (slotsOverride != null) { return slotsOverride; }

        int rows = getRows(page);
        List<Integer> slots = new ArrayList<>();
        for (int i = 1; i <= (rows-3); i++) {
            slots.addAll(Arrays.asList((9 * i + 1), (9 * i + 2), (9 * i + 3), (9 * i + 4), (9 * i + 5), (9 * i + 6), (9 * i + 7)));
        }
        return slots;
    }

    public Pagination<? extends PageItem> getPageItems() {
        return this.items;
    }

    // Overridable
    public @Nonnull String getMenuName(int currentPage, int maxPages) {
        return getMenuName() + " " + (maxPages > 1 ? "(Page " + (currentPage + 1) + "/" + maxPages + ")" : "");
    }

    public abstract String getMenuName();

    public abstract Collection<? extends PageItem> getItems();

    public IBuilder getNextPage() {
        return new ItemBuilder(XMaterial.ARROW).setName("&a&lNext Page &a▶");
    }

    public boolean overridePageItems() {
        return false;
    }

    public IBuilder getPreviousIcon() {
        return new ItemBuilder(XMaterial.ARROW).setName("&a◀ &a&lPrevious Page");
    }

    public int getEmptySlot() {
        return -1;
    }

    public int getFixedSize() {
        return -1;
    }

    public int getLinesFilled(int page) {
        int totalItems = this.items.pageSize();
        int totalItemsPage = this.items.size();
        return Math.min(6, (int) (3 + Math.ceil(totalItemsPage - totalItems * page > totalItems ? 3 : (totalItemsPage - totalItems * page) / 7.0)));
    }

    public int getPreviousSlot() {
        return menu.getInventory().getSize() - 8;
    }

    public int getNextSlot() {
        return menu.getInventory().getSize() - 2;
    }

    public int getRows(int page) {
        return getFixedSize() == -1 ? getLinesFilled(page) : getFixedSize();
    }

    public IBuilder getFillerItem() {
        return null;
    }

    public KamiMenuItem getFillerIcon() {
        return new KamiMenuItem(true, getFillerItem(), -1);
    }

    public void openMenu(Player player, int page) {
        createMenu(player, page).openMenu(player);
    }

    // page 0 indexed
    public KamiMenu createMenu(Player player, int page) {
        String title = getMenuName(page + 1, this.items.totalPages());
        this.menu = new KamiMenu(title, getRows(page));

        // Add previous icon
        if (page > 0) {
            menu.addMenuClick(getPreviousIcon(), (plr, type) -> {
                menu.getIgnoredClose().add(plr.getName());
                openMenu(plr, (page - 1));
                // member.playSound(XSound.UI_BUTTON_CLICK, 1, 2);
            }, getPreviousSlot());
        }

        // Add next icon
        if (items.pageExist(page + 1)) {
            menu.addMenuClick(getNextPage(), (plr, type) -> {
                menu.getIgnoredClose().add(plr.getName());
                openMenu(plr, (page + 1));
                // member.playSound(XSound.UI_BUTTON_CLICK, 1, 2);
            }, getNextSlot());
        }

        // Add other icons
        addOtherIcons();

        // If there are no items to add, open the menu
        if (!items.exists(page)) {
            tryFill();
            return menu;
        }

        // Add all page items
        for (PageItem pageItem : items.getPage(page)) {
            if (pageItem != null && firstEmpty(page) != -1) {
                pageItem.addToMenu(menu, firstEmpty(page));
            }
        }

        // Return the menu !
        tryFill();
        return menu;
    }

    private void tryFill() {
        if (menu == null) { return; }

        // Filler items
        KamiMenuItem fillerItem = getFillerIcon();
        if (fillerItem != null && fillerItem.getIBuilder() != null && fillerItem.isEnabled()) {
            menu.fill(fillerItem.getIBuilder());
        }
    }

    public void addOtherIcons() {
        Collection<KamiMenuItem> otherIcons = supplyOtherIcons();

        // Regular items
        for (KamiMenuItem item : otherIcons) {
            int totalSlots = menu.getRows() * 9;
            if (!item.isEnabled()) { continue; }

            // Set the item
            for (int slot : item.getSlots()) {
                if (slot < 0 || slot >= totalSlots) { continue; }
                item.addToMenu(menu, slot);
            }
        }
    }

    public Collection<KamiMenuItem> supplyOtherIcons() { return new ArrayList<>(); }

    private int firstEmpty(int page) {
        for (int i : getPlacedSlots(currentPage)) {
            if (i >= menu.getSize()) {
                return -1;
            }
            if (menu.getInventory().getItem(i) == null) {
                return i;
            }
        }
        return -1;
    }

    public KamiMenu getMenu() {
        return menu;
    }
}
