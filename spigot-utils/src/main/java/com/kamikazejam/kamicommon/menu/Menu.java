package com.kamikazejam.kamicommon.menu;

import com.kamikazejam.kamicommon.menu.api.clicks.data.MenuClickData;
import com.kamikazejam.kamicommon.menu.api.clicks.data.PlayerClickData;
import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.access.IMenuIconsAccess;
import com.kamikazejam.kamicommon.menu.api.struct.MenuEvents;
import com.kamikazejam.kamicommon.menu.api.struct.MenuOptions;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents the abstraction of a menu into the core parts that {@link MenuManager} needs to interact with.<br>
 * This interface is not meant for public consumption or use, you will find none of the helpful methods you would expect.<br>
 * <br>
 * Use specific menus classes like {@link SimpleMenu} or {@link PaginatedMenu}
 */
@SuppressWarnings("unused")
public interface Menu<M extends Menu<M>> {
    @NotNull MenuEvents<M> getEvents();

    @NotNull MenuOptions<M> getOptions();

    @NotNull MenuSize getMenuSize();

    @NotNull Map<String, MenuIcon<M>> getMenuIcons();

    @NotNull IMenuIconsAccess<M> getMenuIconsAccess();

    @Nullable MenuIcon<M> getFillerIcon();

    /**
     * Attempt to reopen the menu for the given player. Depending on the menu type, this may not be possible for all possible<br>
     * By default, this method will NOT reset the tick counter.<br>
     * See {@link #reopenMenu(boolean)} for reopening with reset.<br>
     */
    void reopenMenu();

    /**
     * Attempt to reopen the menu for the given player. Depending on the menu type, this may not be possible for all possible<br>
     * @param resetTickCounter If true, the tick counter will be reset to 0. This is useful for menus that are not paginated, and<br>
     */
    void reopenMenu(boolean resetTickCounter);

    @Internal
    @NotNull default MenuClickData<M> buildClickData(
            @NotNull M menu,
            @NotNull Player player,
            @NotNull ClickType clickType,
            @NotNull InventoryClickEvent event,
            int page,
            @NotNull MenuIcon<M> icon,
            int iconSlot
    ) {
        return new MenuClickData<>(menu, player, clickType, event, page, icon, iconSlot);
    }

    @Internal
    @NotNull default PlayerClickData<M> buildPlayerClickData(
            @NotNull M menu,
            @NotNull Player player,
            @NotNull ClickType clickType,
            @NotNull InventoryClickEvent event,
            int slot
    ) {
        return new PlayerClickData<>(menu, player, clickType, event, slot);
    }
}
