package com.kamikazejam.kamicommon.menu;

import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.struct.MenuEvents;
import com.kamikazejam.kamicommon.menu.api.struct.MenuOptions;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import com.kamikazejam.kamicommon.menu.paginated.PaginatedMenu;
import com.kamikazejam.kamicommon.menu.simple.SimpleMenu;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Represents the abstraction of a menu into the core parts that {@link MenuManager} needs to interact with.<br>
 * This interface is not meant for public consumption or use, you will find none of the helpful methods you would expect.<br>
 * <br>
 * Use specific menus classes like {@link SimpleMenu} or {@link PaginatedMenu}
 */
public interface Menu {
    @NotNull MenuEvents getEvents();
    @NotNull MenuOptions getOptions();
    @NotNull MenuSize getMenuSize();
    @NotNull Map<String, MenuIcon> getMenuIcons();
}
