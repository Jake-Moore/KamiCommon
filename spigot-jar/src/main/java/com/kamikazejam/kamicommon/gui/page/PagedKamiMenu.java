package com.kamikazejam.kamicommon.gui.page;

import com.kamikazejam.kamicommon.gui.KamiMenu;
import com.kamikazejam.kamicommon.gui.items.MenuItem;
import com.kamikazejam.kamicommon.gui.items.slots.ItemSlot;
import com.kamikazejam.kamicommon.gui.items.slots.LastRowItemSlot;
import com.kamikazejam.kamicommon.gui.items.slots.StaticItemSlot;
import com.kamikazejam.kamicommon.gui.loader.MenuItemLoader;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.xseries.XMaterial;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A wrapper class for {@link KamiMenu} that adds paged functionality to the existing GUI.<br>
 * Note: Slots configured as page icons will override existing items configured from {@link KamiMenu}.<br>
 * Note: {@link KamiMenu} handles all UI logic, this class just manages pagination options and appearance.
 */
@SuppressWarnings("unused")
public class PagedKamiMenu {
    public static final String META_DATA_KEY = "PagedKamiMenu";

    // PagedKamiMenu Data. We use KamiMenu as a 'parent' for UI logic
    private final @NotNull KamiMenu parent;
    @Getter private final @NotNull List<MenuItem> pagedItems; // List for ordering
    @Getter public int currentPage = 0;

    // Configuration Options
    @Setter @Getter private @NotNull Collection<Integer> pageSlots;
    @Getter @Setter private @NotNull MenuItem nextPageIcon = new MenuItem(true, new LastRowItemSlot(7), new ItemBuilder(XMaterial.ARROW).setName("&a&lNext Page &a▶"));
    @Getter @Setter private @NotNull MenuItem prevPageIcon = new MenuItem(true, new LastRowItemSlot(1), new ItemBuilder(XMaterial.ARROW).setName("&a◀ &a&lPrevious Page"));
    @Getter @Setter private boolean appendTitleWithPage = true;

    public PagedKamiMenu(@NotNull KamiMenu parent) {
        this(parent, new ArrayList<MenuItem>());
    }
    public PagedKamiMenu(@NotNull KamiMenu parent, @NotNull List<MenuItem> pagedItems) {
        this.parent = parent;
        this.pagedItems = pagedItems;
        this.pageSlots = defaultPageSlots(parent);
    }

    public PagedKamiMenu(@NotNull KamiMenu parent, @NotNull Collection<Integer> slots) {
        this(parent, new ArrayList<>(), slots);
    }
    public PagedKamiMenu(@NotNull KamiMenu parent, @NotNull List<MenuItem> pagedItems, @NotNull Collection<Integer> slots) {
        this.parent = parent;
        this.pagedItems = pagedItems;
        this.pageSlots = slots;
    }

    public PagedKamiMenu(@NotNull KamiMenu parent, int[] slots) {
        this(parent, new ArrayList<>(), slots);
    }
    public PagedKamiMenu(@NotNull KamiMenu parent, @NotNull List<MenuItem> pagedItems, int[] slots) {
        this.parent = parent;
        this.pagedItems = pagedItems;
        this.pageSlots = new ArrayList<>();
        for (int slot : slots) {
            this.pageSlots.add(slot);
        }
    }

    // Overridable (currentPage is 1 indexed, maxPages is 1 indexed)
    public @NotNull String getMenuName(int currentPage, int maxPages) {
        return this.parent.getTitle() + (maxPages > 1 ? " (Page " + (currentPage) + "/" + maxPages + ")" : "");
    }

    // Utility methods to make it easier to configure the slots of page icons
    public void setPreviousIconSlot(int slot) {
        this.prevPageIcon.setItemSlot(new StaticItemSlot(slot));
    }
    public void setPreviousIconSlot(@NotNull ItemSlot slot) {
        this.prevPageIcon.setItemSlot(slot);
    }
    public void setNextIconSlot(int slot) {
        this.nextPageIcon.setItemSlot(new StaticItemSlot(slot));
    }
    public void setNextIconSlot(@NotNull ItemSlot slot) {
        this.nextPageIcon.setItemSlot(slot);
    }

    // ------------------------------------------------------------ //
    //                        Item Management                       //
    // ------------------------------------------------------------ //
    @CheckReturnValue
    public @NotNull MenuItem addPagedItem(@NotNull IBuilder builder) {
        return this.addPagedItem(new MenuItem(true, new StaticItemSlot(-1), builder));
    }
    @CheckReturnValue
    public @NotNull MenuItem addPagedItem(@NotNull ItemStack stack) {
        return this.addPagedItem(new MenuItem(true, new StaticItemSlot(-1), new ItemBuilder(stack)));
    }

    @CheckReturnValue
    public @NotNull MenuItem addPagedItem(@NotNull String id, @NotNull IBuilder builder) {
        return this.addPagedItem(new MenuItem(true, new StaticItemSlot(-1), builder).setId(id));
    }
    @CheckReturnValue
    public @NotNull MenuItem addPagedItem(@NotNull String id, @NotNull ItemStack stack) {
        return this.addPagedItem(new MenuItem(true, new StaticItemSlot(-1), new ItemBuilder(stack)).setId(id));
    }

    @CheckReturnValue
    public @NotNull MenuItem addPagedItem(@NotNull ConfigurationSection section, @NotNull String key, @Nullable Player player) {
        return this.addPagedItem(MenuItemLoader.load(section.getConfigurationSection(key), player));
    }
    @CheckReturnValue
    public @NotNull MenuItem addPagedItem(@NotNull ConfigurationSection section, @NotNull String key) {
        return this.addPagedItem(MenuItemLoader.load(section.getConfigurationSection(key)));
    }
    @CheckReturnValue
    public @NotNull MenuItem addPagedItem(@NotNull ConfigurationSection section, @Nullable Player player) {
        return this.addPagedItem(MenuItemLoader.load(section, player));
    }
    @CheckReturnValue
    public @NotNull MenuItem addPagedItem(@NotNull ConfigurationSection section) {
        return this.addPagedItem(MenuItemLoader.load(section));
    }

    public @NotNull MenuItem addPagedItem(@NotNull MenuItem menuItem) {
        this.pagedItems.add(menuItem);
        return menuItem;
    }

    public void clearPagedItems() {
        this.pagedItems.clear();
    }

    /**
     * Applies changes to Parent menu, and Opens it for a given player.
     * @param pageIndex The 0-indexed page to open
     */
    public void openMenu(@NotNull Player player, int pageIndex) {
        // createMenu handles the currentPage variable
        applyToParent(pageIndex);
        this.parent.openMenu(player);
    }

    /**
     * Apply relevant page UI icons to the parent menu.
     * @param pageIndex The 0-indexed page to open
     */
    @NotNull
    public KamiMenu applyToParent(int pageIndex) {
        this.parent.getMetaData().put(META_DATA_KEY, this);

        // Calculate the current pagination, this allows the items list to be modified before opening
        Pagination<MenuItem> pagination = new Pagination<>(this.pageSlots.size(), this.pagedItems);

        // Update the title with the pagination data
        if (appendTitleWithPage) {
            this.parent.setTitle(StringUtil.t(getMenuName(pageIndex + 1, pagination.totalPages())));
        }

        // Add previous icon
        if (pageIndex > 0) {
            this.parent.addMenuItem(this.prevPageIcon).setMenuClick((plr, type) -> {
                // We ignore the close since the menu isn't 'closing', just changing pages
                this.parent.getIgnoredClose().add(plr.getUniqueId());
                openMenu(plr, (pageIndex - 1));
            });
        }

        // Add next icon
        if (pagination.pageExist(pageIndex + 1)) {
            this.parent.addMenuItem(this.nextPageIcon).setMenuClick((plr, type) -> {
                this.parent.getIgnoredClose().add(plr.getUniqueId());
                openMenu(plr, (pageIndex + 1));
            });
        }

        // Add all page items
        if (pagination.pageExist(pageIndex)) {
            Collection<Integer> placeableSlots = new ArrayList<>(this.pageSlots);
            for (MenuItem menuItem : pagination.getPage(pageIndex)) {
                if (menuItem == null) { continue; }
                int s = firstEmpty(placeableSlots, this.parent);
                if (s == -1) { break; }

                // Add the page item to the menu
                menuItem.setItemSlot(new StaticItemSlot(s));
                this.parent.addMenuItem(menuItem);
                placeableSlots.remove(s); // Mark it as filled, so firstEmpty moves to the next slot
            }
        }

        // We make some assumptions, like that this menu object belongs to 1 player
        this.parent.whenOpened((plr, v) -> this.currentPage = pageIndex);
        return this.parent;
    }

    private int firstEmpty(@NotNull Collection<Integer> placeableSlots, @NotNull KamiMenu menu) {
        for (int i : placeableSlots) {
            if (i < 0 || i >= menu.getSize()) { continue; }
            if (menu.getInventory().getItem(i) == null) {
                return i;
            }
        }
        return -1;
    }

    @NotNull
    public static List<Integer> defaultPageSlots(@NotNull KamiMenu parent) {
        int rows = (int) Math.ceil(parent.getSize() / 9.0);
        List<Integer> slots = new ArrayList<>();
        // We exclude the top row and bottom 2 rows for nice formatting
        for (int i = 1; i <= (rows-3); i++) {
            // We exclude slot 0 and 8 for nice formatting
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
}
