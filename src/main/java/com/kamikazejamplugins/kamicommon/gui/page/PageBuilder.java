package com.kamikazejamplugins.kamicommon.gui.page;

import com.kamikazejamplugins.kamicommon.gui.KamiMenu;
import com.kamikazejamplugins.kamicommon.gui.interfaces.MenuClickPlayer;
import com.kamikazejamplugins.kamicommon.item.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "SameReturnValue"})
public abstract class PageBuilder<T extends Player> {

    private final int[] middleSlots = new int[]{0, 0, 0, 13, 13, 22, 22, 22};
    //TODO configurability for the placedSlots
    @Getter private final List<Integer> placedSlots = new ArrayList<>(Arrays.asList(
            (9 + 1), (9 + 2), (9 + 3), (9 + 4), (9 + 5), (9 + 6), (9 + 7),
            (9 * 2 + 1), (9 * 2 + 2), (9 * 2 + 3), (9 * 2 + 4), (9 * 2 + 5), (9 * 2 + 6), (9 * 2 + 7),
            (9 * 3 + 1), (9 * 3 + 2), (9 * 3 + 3), (9 * 3 + 4), (9 * 3 + 5), (9 * 3 + 6), (9 * 3 + 7),
            (9 * 4 + 1), (9 * 4 + 2), (9 * 4 + 3), (9 * 4 + 4), (9 * 4 + 5), (9 * 4 + 6), (9 * 4 + 7)));
    @Getter public boolean usingMenu = false;
    @Getter public int currentPage;
    private KamiMenu<T> menu;
    private int maxPages = 0;
    private Pagination<PageItem<T>> items;

    public PageBuilder() {
        this.items = new Pagination<>(21, this.getItems());
    }

    public PageBuilder(List<Integer> slots) {
        this.items = new Pagination<>(slots.size(), this.getItems());
        this.placedSlots.clear();
        placedSlots.addAll(slots);
    }

    public PageBuilder(int[] slots) {
        this.items = new Pagination<>(slots.length, this.getItems());
        this.placedSlots.clear();
        for (int slot : slots) {
            placedSlots.add(slot);
        }
    }

    public PageBuilder(boolean usingMenu) {
        this.usingMenu = usingMenu;
        this.items = new Pagination<>(21, this.getItems());
    }

    public Pagination<PageItem<T>> getPageItems() {
        return this.items;
    }

    public String getMenuName(int currentPage, int maxPages) {
        return null;
    }

    public abstract String getMenuName();

    public abstract List<PageItem<T>> getItems();

    public ItemStack getEmptyItem() {
        return null;
    }

    public ItemBuilder getNextPage() {
        return new ItemBuilder(Material.ARROW).setName("&a&lNext Page &a▶");
    }

    public boolean overridePageItems() {
        return false;
    }

    public ItemBuilder getPreviousPage() {
        return new ItemBuilder(Material.ARROW).setName("&a◀ &a&lPrevious Page");
    }

    public PageItem<T> getBackItem() {
        // null by default.
        return null;
    }

    public int getEmptySlot() {
        return -1;
    }

    public boolean reinitializeOnOpen() {
        return false;
    }

    public int getFixedSize() {
        return -1;
    }

    public int getLinesFilled(int page) {
        int totalItems = this.items.pageSize();
        int totalItemsPage = this.items.size();
        return Math.min(6, (int) (3 + Math.ceil(totalItemsPage - totalItems * page > totalItems ? 3 : (totalItemsPage - totalItems * page) / 7.0)));
    }

    public PageBuilder<T> apply() {
        this.maxPages = (int) Math.ceil(getItems().size() / (double) placedSlots.size());
        if (maxPages <= 0) {
            maxPages = 1;
        }
        return this;
    }

    public int getPreviousSlot() {
        return 46;
    }

    public int getNextSlot() {
        return 52;
    }

    public void openMenu(Player p, int page) {
        String title = getMenuName(page + 1, maxPages);
        if (title == null) {
            title = getMenuName() + " " + (maxPages > 1 ? "(Page " + (page + 1) + "/" + maxPages + ")" : "");
        }
        this.menu = new KamiMenu<>(title, getFixedSize() == -1 ? getLinesFilled(page) : getFixedSize());

        if (page > 0) {
            ItemBuilder builder = getPreviousPage();
            menu.addMenuClick(builder, (member, type) -> {
                menu.getIgnoredClose().add(member.getName());
                openMenu(member, (page - 1));
                //member.playSound(XSound.UI_BUTTON_CLICK, 1, 2);
            }, getPreviousSlot() >= menu.getInventory().getSize() ? menu.getInventory().getSize() - 8 : getPreviousSlot());
        }

        if (items.pageExist(page + 1)) {
            ItemBuilder builder = getNextPage();
            menu.addMenuClick(builder, (member, type) -> {
                menu.getIgnoredClose().add(member.getName());
                openMenu(member, (page + 1));
                //member.playSound(XSound.UI_BUTTON_CLICK, 1, 2);
            }, getNextSlot() >= menu.getInventory().getSize() ? menu.getInventory().getSize() - 2 : getNextSlot());
        }

        if (getBackItem() != null) {
            menu.addMenuClick(getBackItem().getItemStack(), getBackItem().getMenuClick(), (menu.getInventory().getSize() - 5));
        }

        if (reinitializeOnOpen()) {
            this.items = new Pagination<>(21, this.getItems());
        }

        if (this.items.isEmpty() && this.getEmptyItem() != null) {
            menu.setItem(getEmptyItem(), getEmptySlot() != -1 ? getEmptySlot() : middleSlots[getLinesFilled(page)]);
        } else {
            if (items.exists(page)) {
                for (PageItem<T> pageItem : items.getPage(page)) {
                    if (pageItem != null) {
                        if (firstEmpty() != -1) {
                            MenuClickPlayer<T> menuClick = pageItem.getMenuClick();
                            if (menuClick == null) {
                                menu.setItem(firstEmpty(), pageItem.getItemStack());
                            } else {
                                menu.addMenuClick(pageItem.getItemStack(), menuClick, firstEmpty());
                            }
                        }
                    }
                }
            }
        }

        menu.openMenu(p);
    }

    private int firstEmpty() {
        for (int i : placedSlots) {
            if (i >= menu.getSize()) {
                return -1;
            }
            if (menu.getInventory().getItem(i) == null) {
                return i;
            }
        }
        return -1;
    }

    public KamiMenu<T> getMenu() {
        return menu;
    }
}
