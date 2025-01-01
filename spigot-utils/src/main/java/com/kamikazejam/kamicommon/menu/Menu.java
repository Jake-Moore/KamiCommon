package com.kamikazejam.kamicommon.menu;

import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.struct.MenuEvents;
import com.kamikazejam.kamicommon.menu.api.struct.MenuOptions;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import com.kamikazejam.kamicommon.menu.paginated.PaginatedMenu;
import com.kamikazejam.kamicommon.menu.simple.SimpleMenu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

    /**
     * Attempt to reopen the menu for the given player. Depending on the menu type, this may not be possible for all possible
     * {@link Player} objects. For instance, the {@link SimpleMenu} requires that the same player from that menu be passed.
     */
    void reopenMenu(@NotNull Player player);
}
