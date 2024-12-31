package com.kamikazejam.kamicommon.menu.items.access;

import com.kamikazejam.kamicommon.menu.items.MenuItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class MenuItemsAccess implements IMenuItemsAccess {
    private final @NotNull Map<String, MenuItem> menuItemMap;
    public MenuItemsAccess(@NotNull Map<String, MenuItem> menuItemMap) {
        this.menuItemMap = menuItemMap;
    }

    // ------------------------------------------------------------ //
    //                        Item Management                       //
    // ------------------------------------------------------------ //
    @Override
    public @NotNull MenuItem addMenuItem(@NotNull MenuItem menuItem) {
        this.menuItemMap.put(menuItem.getId(), menuItem);
        return menuItem;
    }

    @Override
    public @Nullable MenuItem removeMenuItem(@NotNull String id) {
        return this.menuItemMap.remove(id);
    }

    @Override
    public void clearMenuItems() {
        this.menuItemMap.clear();
    }



    // ------------------------------------------------------------ //
    //                   Item Management (by ID)                    //
    // ------------------------------------------------------------ //
    @NotNull
    public Optional<MenuItem> getMenuItem(@NotNull String id) {
        if (!this.menuItemMap.containsKey(id)) { return Optional.empty(); }
        return Optional.ofNullable(this.menuItemMap.get(id));
    }
    public boolean isValidMenuItemID(@NotNull String id) {
        return this.menuItemMap.containsKey(id);
    }
    @NotNull
    public Set<String> getMenuItemIDs() {
        return this.menuItemMap.keySet();
    }
}
