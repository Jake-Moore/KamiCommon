package com.kamikazejam.kamicommon.menu.api.icons.access;

import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import com.kamikazejam.kamicommon.menu.api.icons.slots.IconSlot;
import com.kamikazejam.kamicommon.menu.api.icons.slots.PositionSlot;
import com.kamikazejam.kamicommon.menu.api.struct.icons.PrioritizedMenuIconMap;
import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public class MenuIconsAccess implements IMenuIconsAccess {
    private final @NotNull MenuSize menuSize;
    private final @NotNull PrioritizedMenuIconMap menuIcons;
    public MenuIconsAccess(@NotNull MenuSize menuSize, @NotNull PrioritizedMenuIconMap menuIcons) {
        this.menuSize = menuSize;
        this.menuIcons = menuIcons;
    }

    // ------------------------------------------------------------ //
    //                        Icon Management                       //
    // ------------------------------------------------------------ //
    @Override
    public @NotNull MenuIcon setMenuIcon(@NotNull MenuIcon menuIcon, @Nullable IconSlot iconSlot) {
        this.menuIcons.add(menuIcon, iconSlot);
        return menuIcon;
    }

    @Override
    public @Nullable MenuIcon removeMenuIcon(@NotNull String id) {
        return this.menuIcons.remove(id);
    }

    public @Nullable Set<MenuIcon> removeMenuIcon(int slot) {
        return this.menuIcons.remove(slot, this.menuSize);
    }

    @Override
    public void clearMenuIcons() {
        this.menuIcons.clear();
    }



    // ------------------------------------------------------------ //
    //                   Icon Management (by ID)                    //
    // ------------------------------------------------------------ //
    @Override
    public @NotNull Optional<MenuIcon> getMenuIcon(@NotNull String id) {
        return this.menuIcons.get(id);
    }
    @Override
    public boolean isValidMenuIconID(@NotNull String id) {
        return this.menuIcons.contains(id);
    }
    @Override
    public @NotNull Set<String> getMenuIconIDs() {
        return this.menuIcons.keySet();
    }


    // ------------------------------------------------------------ //
    //                  Icon Management (by slot)                   //
    // ------------------------------------------------------------ //

    @Override
    public @NotNull Optional<MenuIcon> getMenuIcon(int slot) {
        return Optional.ofNullable(this.menuIcons.getActiveIconForSlot(this.menuSize, slot));
    }

    @Override
    public boolean hasMenuIcon(int slot) {
        return this.menuIcons.containsActiveIconForSlot(this.menuSize, slot);
    }

    // ------------------------------------------------------------ //
    //                  Icon Management (by position)               //
    // ------------------------------------------------------------ //
    @Override
    public @NotNull Optional<MenuIcon> getMenuIcon(@NotNull PositionSlot slot) {
        return this.getMenuIcon(this.menuSize.mapPositionToSlot(slot.getRow(), slot.getCol()));
    }
    @Override
    public boolean hasMenuIcon(@NotNull PositionSlot slot) {
        return this.hasMenuIcon(this.menuSize.mapPositionToSlot(slot.getRow(), slot.getCol()));
    }
}
