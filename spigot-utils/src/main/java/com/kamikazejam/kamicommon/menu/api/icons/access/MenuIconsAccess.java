package com.kamikazejam.kamicommon.menu.api.icons.access;

import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class MenuIconsAccess implements IMenuIconsAccess {
    private final @NotNull Map<String, MenuIcon> menuIconMap;
    public MenuIconsAccess(@NotNull Map<String, MenuIcon> menuIconMap) {
        this.menuIconMap = menuIconMap;
    }

    // ------------------------------------------------------------ //
    //                        Icon Management                       //
    // ------------------------------------------------------------ //
    @Override
    public @NotNull MenuIcon addMenuIcon(@NotNull MenuIcon menuIcon) {
        this.menuIconMap.put(menuIcon.getId(), menuIcon);
        return menuIcon;
    }

    @Override
    public @Nullable MenuIcon removeMenuIcon(@NotNull String id) {
        return this.menuIconMap.remove(id);
    }

    @Override
    public void clearMenuIcons() {
        this.menuIconMap.clear();
    }



    // ------------------------------------------------------------ //
    //                   Icon Management (by ID)                    //
    // ------------------------------------------------------------ //
    @NotNull
    public Optional<MenuIcon> getMenuIcon(@NotNull String id) {
        if (!this.menuIconMap.containsKey(id)) { return Optional.empty(); }
        return Optional.ofNullable(this.menuIconMap.get(id));
    }
    public boolean isValidMenuIconID(@NotNull String id) {
        return this.menuIconMap.containsKey(id);
    }
    @NotNull
    public Set<String> getMenuIconIDs() {
        return this.menuIconMap.keySet();
    }
}
