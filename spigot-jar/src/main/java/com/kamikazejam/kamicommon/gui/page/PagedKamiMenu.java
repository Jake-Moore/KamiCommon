package com.kamikazejam.kamicommon.gui.page;

import com.kamikazejam.kamicommon.gui.KamiMenu;
import com.kamikazejam.kamicommon.gui.clicks.MenuClick;
import com.kamikazejam.kamicommon.gui.clicks.MenuClickEvent;
import com.kamikazejam.kamicommon.gui.clicks.MenuClickPage;
import com.kamikazejam.kamicommon.gui.items.MenuItem;
import com.kamikazejam.kamicommon.gui.items.interfaces.IBuilderModifier;
import com.kamikazejam.kamicommon.gui.items.slots.ItemSlot;
import com.kamikazejam.kamicommon.gui.items.slots.LastRowItemSlot;
import com.kamikazejam.kamicommon.gui.items.slots.StaticItemSlot;
import com.kamikazejam.kamicommon.gui.loader.MenuItemLoader;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.item.ItemBuilder;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A wrapper class for {@link KamiMenu} that adds paged functionality to the existing GUI.<br>
 * Note: Slots configured as page icons will override existing items configured from {@link KamiMenu}.<br>
 * Note: {@link KamiMenu} handles all UI logic, this class just manages pagination options and appearance.
 */
@SuppressWarnings("unused")
public class PagedKamiMenu {
    @AllArgsConstructor @Getter
    public static class IndexedMenuItem {
        public final @NotNull MenuItem item;
        public final int index;
    }

    public static final String META_DATA_KEY = "PagedKamiMenu";

    // PagedKamiMenu Data. We use KamiMenu as a 'parent' for UI logic
    private final @NotNull KamiMenu parent;
    @Getter private final Map<String, IndexedMenuItem> pagedItems = new HashMap<>(); // Uses IndexedMenuItem for ordering
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
        pagedItems.forEach(this::addPagedItem);
        this.pageSlots = defaultPageSlots(parent);
    }

    public PagedKamiMenu(@NotNull KamiMenu parent, @NotNull Collection<Integer> slots) {
        this(parent, new ArrayList<>(), slots);
    }
    public PagedKamiMenu(@NotNull KamiMenu parent, @NotNull List<MenuItem> pagedItems, @NotNull Collection<Integer> slots) {
        this.parent = parent;

        this.pageSlots = slots;
    }

    public PagedKamiMenu(@NotNull KamiMenu parent, int[] slots) {
        this(parent, new ArrayList<>(), slots);
    }
    public PagedKamiMenu(@NotNull KamiMenu parent, @NotNull List<MenuItem> pagedItems, int[] slots) {
        this.parent = parent;
        pagedItems.forEach(this::addPagedItem);
        this.pageSlots = new ArrayList<>();
        for (int slot : slots) {
            this.pageSlots.add(slot);
        }
    }

    // Overridable (currentPage is 1 indexed, maxPages is 1 indexed)
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private transient @Nullable String cachedTitle = null;
    public @NotNull String getMenuName(int currentPage, int maxPages) {
        // We use a cached title, since after the first modification to include page information
        // We no longer can request the original title
        String base = (cachedTitle == null) ? cachedTitle = this.parent.getTitle() : cachedTitle;
        return base + (maxPages > 1 ? " (Page " + (currentPage) + "/" + maxPages + ")" : "");
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
        return this.addPagedItem(new IndexedMenuItem(menuItem, this.pagedItems.size()));
    }
    public @NotNull MenuItem addPagedItem(@NotNull IndexedMenuItem indexed) {
        if (this.pagedItems.containsKey(indexed.item.getId())) {
            // throw error so the developer can fix it
            throw new IllegalArgumentException("Duplicate MenuItem ID in PagedKamiMenu: '" + indexed.item.getId() + "'. Existing IDs are: " + Arrays.toString(this.getPagedItemIDs().toArray(new String[0])));
        }
        this.pagedItems.put(indexed.item.getId(), indexed);
        return indexed.item;
    }

    public void clearPagedItems() {
        this.pagedItems.clear();
    }

    // ------------------------------------------------------------ //
    //                   Item Management (by ID)                    //
    // ------------------------------------------------------------ //
    /**
     * Retrieve a menu item by its id
     */
    @NotNull
    public Optional<MenuItem> getMenuItem(@NotNull String id) {
        if (!pagedItems.containsKey(id)) { return Optional.empty(); }
        return Optional.ofNullable(pagedItems.get(id)).map(IndexedMenuItem::getItem);
    }

    @NotNull
    public PagedKamiMenu setMenuClick(@NotNull String id, @NotNull MenuClick click) {
        this.getMenuItem(id).ifPresent(item -> item.setMenuClick(click));
        return this;
    }
    @NotNull
    public PagedKamiMenu setMenuClick(@NotNull String id, @NotNull MenuClickPage click) {
        this.getMenuItem(id).ifPresent(item -> item.setMenuClick(click));
        return this;
    }
    @NotNull
    public PagedKamiMenu setMenuClick(@NotNull String id, @NotNull MenuClickEvent click) {
        this.getMenuItem(id).ifPresent(item -> item.setMenuClick(click));
        return this;
    }
    @NotNull
    public PagedKamiMenu setModifier(@NotNull String id, @NotNull IBuilderModifier modifier) {
        this.getMenuItem(id).ifPresent(item -> item.setModifier(modifier));
        return this;
    }
    @NotNull
    public PagedKamiMenu setAutoUpdate(@NotNull String id, @NotNull IBuilderModifier modifier, int tickInterval) {
        this.getMenuItem(id).ifPresent(item -> item.setAutoUpdate(modifier, tickInterval));
        return this;
    }
    public boolean isValidPagedItemID(@NotNull String id) {
        return pagedItems.containsKey(id);
    }
    @NotNull
    public Set<String> getPagedItemIDs() {
        return pagedItems.keySet();
    }



    /**
     * Applies changes to Parent menu, and Opens it for a given player.
     * @param pageIndex The 0-indexed page to open
     */
    public void openMenu(@NotNull Player player, int pageIndex) {
        // createMenu handles the currentPage variable
        this.applyToParent(pageIndex).openMenu(player);
    }

    /**
     * Apply relevant page UI icons to the parent menu.
     * @param pageIndex The 0-indexed page to open
     */
    @NotNull
    public KamiMenu applyToParent(int pageIndex) {
        if (this.pageSlots.isEmpty()) {
            throw new IllegalStateException("No page slots have been configured for a PagedKamiMenu, cannot open a page without slots!!!");
        }

        // ***** WARNING *****
        // All changes to KamiMenu are persisted between calls
        // It is imperative that data from previous calls is removed if necessary
        // For example, title may already contain pagination info, inventory may contain paged items, etc.
        // ***** WARNING *****
        this.parent.getMetaData().put(META_DATA_KEY, this);

        // Calculate the current pagination, this allows the items list to be modified before opening
        List<MenuItem> ordered = this.pagedItems.values().stream()
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparingInt(IndexedMenuItem::getIndex))
                        .map(IndexedMenuItem::getItem)
                        .toList();
        Pagination<MenuItem> pagination = new Pagination<>(this.pageSlots.size(), ordered);

        // Update the title with the pagination data
        if (appendTitleWithPage) {
            // getMenuName uses a cached value from the first time it was called, so that it doesn't change
            this.parent.setTitle(StringUtil.t(getMenuName(pageIndex + 1, pagination.totalPages())));
            // We must recreate a new inventory to use a new title
            this.parent.recreateInventory();
        }

        // Add previous icon
        final String nextId = "PagedKamiMenu-Next";
        this.parent.removeMenuItem(nextId); // Remove cached icon
        if (pageIndex > 0) {
            this.parent.addMenuItem(this.prevPageIcon.setId(nextId)).setMenuClick((plr, type) -> {
                // We ignore the close since the menu isn't 'closing', just changing pages
                this.parent.getIgnoredClose().add(plr.getUniqueId());
                openMenu(plr, (pageIndex - 1));
            });
        }

        // Add next icon
        final String prevId = "PagedKamiMenu-Prev";
        this.parent.removeMenuItem(prevId); // Remove cached icon
        if (pagination.pageExist(pageIndex + 1)) {
            this.parent.addMenuItem(this.nextPageIcon.setId(prevId)).setMenuClick((plr, type) -> {
                this.parent.getIgnoredClose().add(plr.getUniqueId());
                openMenu(plr, (pageIndex + 1));
            });
        }

        // Remove all items within the paged slots
        final String pagedItemId = "PagedKamiMenu-Item";
        this.parent.getMenuItems().entrySet().removeIf(entry -> entry.getKey().startsWith(pagedItemId));

        // Add all page items
        if (pagination.pageExist(pageIndex)) {
            Collection<Integer> placeableSlots = new ArrayList<>(this.pageSlots);
            for (MenuItem menuItem : pagination.getPage(pageIndex)) {
                if (menuItem == null) { continue; }
                int s = firstEmpty(placeableSlots, this.parent);
                if (s == -1) { break; }

                // Add the page item to the menu
                MenuItem item = menuItem.copy().setId(pagedItemId + "-" + s).setItemSlot(new StaticItemSlot(s));
                this.parent.addMenuItem(item);
                placeableSlots.remove(s); // Mark it as filled, so firstEmpty moves to the next slot
            }
        }

        // We make some assumptions, like that this menu object belongs to 1 player
        final String callbackId = "PagedKamiMenu-Callback";
        this.parent.setOpenCallback(callbackId, (plr, v) -> this.currentPage = pageIndex);
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
