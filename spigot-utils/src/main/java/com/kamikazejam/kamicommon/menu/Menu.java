package com.kamikazejam.kamicommon.menu;

import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.struct.MenuEvents;
import com.kamikazejam.kamicommon.menu.api.struct.MenuOptions;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface Menu {
    @NotNull MenuEvents getEvents();
    @NotNull MenuOptions getOptions();
    @NotNull MenuSize getMenuSize();
    @NotNull Map<String, MenuIcon> getMenuIcons();
}
