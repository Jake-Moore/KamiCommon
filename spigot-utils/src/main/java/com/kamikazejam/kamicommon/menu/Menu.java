package com.kamikazejam.kamicommon.menu;

import com.kamikazejam.kamicommon.menu.items.MenuItem;
import com.kamikazejam.kamicommon.menu.struct.MenuEvents;
import com.kamikazejam.kamicommon.menu.struct.MenuOptions;
import com.kamikazejam.kamicommon.menu.struct.size.MenuSize;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface Menu {
    @NotNull MenuEvents getEvents();
    @NotNull MenuOptions getOptions();
    @NotNull MenuSize getMenuSize();
    @NotNull Map<String, MenuItem> getMenuItems();
}
