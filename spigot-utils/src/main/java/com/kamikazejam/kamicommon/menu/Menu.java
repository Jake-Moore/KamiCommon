package com.kamikazejam.kamicommon.menu;

import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.access.IMenuIconsAccess;
import com.kamikazejam.kamicommon.menu.api.struct.MenuEvents;
import com.kamikazejam.kamicommon.menu.api.struct.MenuOptions;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import org.bukkit.entity.Player;
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
public interface Menu {
    @NotNull MenuEvents getEvents();
    @NotNull MenuOptions getOptions();
    @NotNull MenuSize getMenuSize();
    @NotNull Map<String, MenuIcon> getMenuIcons();
    @NotNull IMenuIconsAccess getMenuIconsAccess();
    @Nullable MenuIcon getFillerIcon();

    /**
     * Attempt to reopen the menu for the given player. Depending on the menu type, this may not be possible for all possible<br>
     * By default, this method will NOT reset the tick counter.<br>
     * See {@link #reopenMenu(Player, boolean)} for reopening with reset.<br>
     * {@link Player} objects. For instance, the {@link SimpleMenu} requires that the same player from that menu be passed.
     */
    void reopenMenu(@NotNull Player player);

    /**
     * Attempt to reopen the menu for the given player. Depending on the menu type, this may not be possible for all possible<br>
     * @param resetTickCounter If true, the tick counter will be reset to 0. This is useful for menus that are not paginated, and<br>
     * {@link Player} objects. For instance, the {@link SimpleMenu} requires that the same player from that menu be passed.
     */
    void reopenMenu(@NotNull Player player, boolean resetTickCounter);
}
