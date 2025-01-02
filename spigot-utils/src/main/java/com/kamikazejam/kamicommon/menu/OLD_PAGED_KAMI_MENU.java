//package com.kamikazejam.kamicommon.menu;
//
//import com.kamikazejam.kamicommon.menu.api.icons.PrioritizedMenuIcon;
//import com.kamikazejam.kamicommon.menu.clicks.MenuClick;
//import com.kamikazejam.kamicommon.menu.clicks.MenuClickEvent;
//import com.kamikazejam.kamicommon.menu.clicks.MenuClickPage;
//import com.kamikazejam.kamicommon.menu.items.MenuItem;
//import com.kamikazejam.kamicommon.menu.items.interfaces.IBuilderModifier;
//import com.kamikazejam.kamicommon.menu.items.slots.ItemSlot;
//import com.kamikazejam.kamicommon.menu.items.slots.LastRowItemSlot;
//import com.kamikazejam.kamicommon.menu.items.slots.StaticItemSlot;
//import com.kamikazejam.kamicommon.menu.loaders.MenuItemLoader;
//import com.kamikazejam.kamicommon.item.IBuilder;
//import com.kamikazejam.kamicommon.item.ItemBuilder;
//import com.kamikazejam.kamicommon.menu.util.Pagination;
//import com.kamikazejam.kamicommon.util.StringUtil;
//import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.ItemStack;
//import org.jetbrains.annotations.CheckReturnValue;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.*;
//
//public class OLD_PAGED_KAMI_MENU {
//
//    // Utility methods to make it easier to configure the slots of page icons
//    public void setPreviousIconSlot(int slot) {
//        this.prevPageIcon.setItemSlot(new StaticItemSlot(slot));
//    }
//    public void setPreviousIconSlot(@NotNull ItemSlot slot) {
//        this.prevPageIcon.setItemSlot(slot);
//    }
//    public void setNextIconSlot(int slot) {
//        this.nextPageIcon.setItemSlot(new StaticItemSlot(slot));
//    }
//    public void setNextIconSlot(@NotNull ItemSlot slot) {
//        this.nextPageIcon.setItemSlot(slot);
//    }
//
//    // ------------------------------------------------------------ //
//    //                        Item Management                       //
//    // ------------------------------------------------------------ //
//    @CheckReturnValue
//    public @NotNull MenuItem addPagedItem(@NotNull IBuilder builder) {
//        return this.addPagedItem(new MenuItem(true, new StaticItemSlot(-1), builder));
//    }
//    @CheckReturnValue
//    public @NotNull MenuItem addPagedItem(@NotNull ItemStack stack) {
//        return this.addPagedItem(new MenuItem(true, new StaticItemSlot(-1), new ItemBuilder(stack)));
//    }
//
//    @CheckReturnValue
//    public @NotNull MenuItem addPagedItem(@NotNull String id, @NotNull IBuilder builder) {
//        return this.addPagedItem(new MenuItem(true, new StaticItemSlot(-1), builder).setId(id));
//    }
//    @CheckReturnValue
//    public @NotNull MenuItem addPagedItem(@NotNull String id, @NotNull ItemStack stack) {
//        return this.addPagedItem(new MenuItem(true, new StaticItemSlot(-1), new ItemBuilder(stack)).setId(id));
//    }
//
//    @CheckReturnValue
//    public @NotNull MenuItem addPagedItem(@NotNull ConfigurationSection section, @NotNull String key, @Nullable Player player) {
//        return this.addPagedItem(MenuItemLoader.load(section.getConfigurationSection(key), player));
//    }
//    @CheckReturnValue
//    public @NotNull MenuItem addPagedItem(@NotNull ConfigurationSection section, @NotNull String key) {
//        return this.addPagedItem(MenuItemLoader.load(section.getConfigurationSection(key)));
//    }
//    @CheckReturnValue
//    public @NotNull MenuItem addPagedItem(@NotNull ConfigurationSection section, @Nullable Player player) {
//        return this.addPagedItem(MenuItemLoader.load(section, player));
//    }
//    @CheckReturnValue
//    public @NotNull MenuItem addPagedItem(@NotNull ConfigurationSection section) {
//        return this.addPagedItem(MenuItemLoader.load(section));
//    }
//
//    public @NotNull MenuItem addPagedItem(@NotNull MenuItem menuItem) {
//        return this.addPagedItem(new PrioritizedMenuIcon(menuItem, this.pagedItems.size()));
//    }
//    public @NotNull MenuItem addPagedItem(@NotNull PrioritizedMenuIcon indexed) {
//        if (this.pagedItems.containsKey(indexed.item.getId())) {
//            // throw error so the developer can fix it
//            throw new IllegalArgumentException("Duplicate MenuItem ID in PagedKamiMenu: '" + indexed.item.getId() + "'. Existing IDs are: " + Arrays.toString(this.getPagedItemIDs().toArray(new String[0])));
//        }
//        this.pagedItems.put(indexed.item.getId(), indexed);
//        return indexed.item;
//    }
//
//    public void clearPagedItems() {
//        this.pagedItems.clear();
//    }
//
//    // ------------------------------------------------------------ //
//    //                   Item Management (by ID)                    //
//    // ------------------------------------------------------------ //
//    /**
//     * Retrieve a menu item by its id
//     */
//    @NotNull
//    public Optional<MenuItem> getMenuItem(@NotNull String id) {
//        if (!pagedItems.containsKey(id)) { return Optional.empty(); }
//        return Optional.ofNullable(pagedItems.get(id)).map(PrioritizedMenuIcon::getItem);
//    }
//
//    @NotNull
//    public OLD_PAGED_KAMI_MENU setMenuClick(@NotNull String id, @NotNull MenuClick click) {
//        this.getMenuItem(id).ifPresent(item -> item.setMenuClick(click));
//        return this;
//    }
//    @NotNull
//    public OLD_PAGED_KAMI_MENU setMenuClick(@NotNull String id, @NotNull MenuClickPage click) {
//        this.getMenuItem(id).ifPresent(item -> item.setMenuClick(click));
//        return this;
//    }
//    @NotNull
//    public OLD_PAGED_KAMI_MENU setMenuClick(@NotNull String id, @NotNull MenuClickEvent click) {
//        this.getMenuItem(id).ifPresent(item -> item.setMenuClick(click));
//        return this;
//    }
//    @NotNull
//    public OLD_PAGED_KAMI_MENU setModifier(@NotNull String id, @NotNull IBuilderModifier modifier) {
//        this.getMenuItem(id).ifPresent(item -> item.setModifier(modifier));
//        return this;
//    }
//    @NotNull
//    public OLD_PAGED_KAMI_MENU setAutoUpdate(@NotNull String id, @NotNull IBuilderModifier modifier, int tickInterval) {
//        this.getMenuItem(id).ifPresent(item -> item.setAutoUpdate(modifier, tickInterval));
//        return this;
//    }
//    public boolean isValidPagedItemID(@NotNull String id) {
//        return pagedItems.containsKey(id);
//    }
//    @NotNull
//    public Set<String> getPagedItemIDs() {
//        return pagedItems.keySet();
//    }
//
//
//
//    /**
//     * Applies changes to Parent menu, and Opens it for a given player.
//     * @param pageIndex The 0-indexed page to open
//     */
//    public void openMenu(@NotNull Player player, int pageIndex) {
//        // createMenu handles the currentPage variable
//        this.applyToParent(pageIndex).openMenu(player);
//    }
//
//    /**
//     * Apply relevant page UI icons to the parent menu.
//     * @param pageIndex The 0-indexed page to open
//     */
//    @NotNull
//    public OLD_KAMI_MENU applyToParent(int pageIndex) {
//        if (this.pageSlots.isEmpty()) {
//            throw new IllegalStateException("No page slots have been configured for a PagedKamiMenu, cannot open a page without slots!!!");
//        }
//
//        // ***** WARNING *****
//        // All changes to KamiMenu are persisted between calls
//        // It is imperative that data from previous calls is removed if necessary
//        // For example, title may already contain pagination info, inventory may contain paged items, etc.
//        // ***** WARNING *****
//        this.parent.getMetaData().put(META_DATA_KEY, this);
//
//        // Calculate the current pagination, this allows the items list to be modified before opening
//        List<MenuItem> ordered = this.pagedItems.values().stream()
//                        .filter(Objects::nonNull)
//                        .sorted(Comparator.comparingInt(PrioritizedMenuIcon::getPriority))
//                        .map(PrioritizedMenuIcon::getItem)
//                        .toList();
//        Pagination<MenuItem> pagination = new Pagination<>(this.pageSlots.size(), ordered);
//
//        // Update the title with the pagination data
//        if (appendTitleWithPage) {
//            // getMenuName uses a cached value from the first time it was called, so that it doesn't change
//            this.parent.setTitle(StringUtil.t(getMenuName(pageIndex + 1, pagination.totalPages())));
//            // We must recreate a new inventory to use a new title
//            this.parent.recreateInventory();
//        }
//
//        // Add previous icon
//        final String nextId = "PagedKamiMenu-Next";
//        this.parent.removeMenuItem(nextId); // Remove cached icon
//        if (pageIndex > 0) {
//            this.parent.addMenuItem(this.prevPageIcon.setId(nextId)).setMenuClick((plr, type) -> {
//                // We ignore the close since the menu isn't 'closing', just changing pages
//                this.parent.getIgnoredClose().add(plr.getUniqueId()); // TODO HANDLE THIS BETTER WITH INTERNAL STATE
//                openMenu(plr, (pageIndex - 1));
//            });
//        }
//
//        // Add next icon
//        final String prevId = "PagedKamiMenu-Prev";
//        this.parent.removeMenuItem(prevId); // Remove cached icon
//        if (pagination.pageExist(pageIndex + 1)) {
//            this.parent.addMenuItem(this.nextPageIcon.setId(prevId)).setMenuClick((plr, type) -> {
//                this.parent.getIgnoredClose().add(plr.getUniqueId()); // TODO HANDLE THIS BETTER WITH INTERNAL STATE
//                openMenu(plr, (pageIndex + 1));
//            });
//        }
//
//        // Remove all items within the paged slots
//        final String pagedItemId = "PagedKamiMenu-Item";
//        this.parent.getMenuItems().entrySet().removeIf(entry -> entry.getKey().startsWith(pagedItemId));
//
//        // Add all page items
//        if (pagination.pageExist(pageIndex)) {
//            Collection<Integer> placeableSlots = new ArrayList<>(this.pageSlots);
//            for (MenuItem menuItem : pagination.getPage(pageIndex)) {
//                if (menuItem == null) { continue; }
//                int s = firstEmpty(placeableSlots, this.parent);
//                if (s == -1) { break; }
//
//                // Add the page item to the menu
//                MenuItem item = menuItem.copy().setId(pagedItemId + "-" + s).setItemSlot(new StaticItemSlot(s));
//                this.parent.addMenuItem(item);
//                placeableSlots.remove(s); // Mark it as filled, so firstEmpty moves to the next slot
//            }
//        }
//
//        // We make some assumptions, like that this menu object belongs to 1 player
//        final String callbackId = "PagedKamiMenu-Callback";
//        this.parent.setOpenCallback(callbackId, (plr, v) -> this.currentPage = pageIndex);
//        return this.parent;
//    }
//
//    private int firstEmpty(@NotNull Collection<Integer> placeableSlots, @NotNull OLD_KAMI_MENU menu) {
//        for (int i : placeableSlots) {
//            if (i < 0 || i >= menu.getSize()) { continue; }
//            if (menu.getInventory().getItem(i) == null) {
//                return i;
//            }
//        }
//        return -1;
//    }
//
//    @NotNull
//    public static List<Integer> defaultPageSlots(@NotNull OLD_KAMI_MENU parent) {
//        int rows = (int) Math.ceil(parent.getSize() / 9.0);
//        List<Integer> slots = new ArrayList<>();
//        // We exclude the top row and bottom 2 rows for nice formatting
//        for (int i = 1; i <= (rows-3); i++) {
//            // We exclude slot 0 and 8 for nice formatting
//            slots.add(9 * i + 1);
//            slots.add(9 * i + 2);
//            slots.add(9 * i + 3);
//            slots.add(9 * i + 4);
//            slots.add(9 * i + 5);
//            slots.add(9 * i + 6);
//            slots.add(9 * i + 7);
//        }
//        return slots;
//    }
//}
