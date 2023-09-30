package com.kamikazejamplugins.kamicommon.gui.page;

import com.kamikazejamplugins.kamicommon.gui.interfaces.Menu;
import com.kamikazejamplugins.kamicommon.gui.interfaces.MenuClick;
import com.kamikazejamplugins.kamicommon.gui.interfaces.MenuClickPlayer;
import com.kamikazejamplugins.kamicommon.item.IBuilder;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings({"unused", "UnusedReturnValue"})
@Getter @Setter
public class PageItem {

    public @Nonnull IBuilder iBuilder;

    // Only one should ever be set
    public @Nullable MenuClick menuClick = null;
    public @Nullable MenuClickPlayer menuClickPlayer = null;

    public PageItem(@Nonnull IBuilder iBuilder, @Nullable MenuClick menuClick) {
        this.iBuilder = iBuilder;
        this.menuClick = menuClick;
    }

    public PageItem(@Nonnull IBuilder iBuilder, @Nullable MenuClickPlayer menuClickPlayer) {
        this.iBuilder = iBuilder;
        this.menuClickPlayer = menuClickPlayer;
    }

    public PageItem setIBuilder(@Nonnull IBuilder iBuilder) {
        this.iBuilder = iBuilder;
        return this;
    }

    public PageItem setMenuClick(MenuClick menuClick) {
        this.menuClick = menuClick;
        this.menuClickPlayer = null;
        return this;
    }

    public PageItem setMenuClick(MenuClickPlayer menuClickPlayer) {
        this.menuClickPlayer = menuClickPlayer;
        this.menuClick = null;
        return this;
    }

    public Menu addToMenu(Menu menu, int slot) {
        // For no click
        if (menuClick == null && menuClickPlayer == null) {
            menu.setItem(slot, iBuilder);
            return menu;
        }

        // For player click
        if (menuClickPlayer != null) {
            menu.addMenuClick(iBuilder, menuClickPlayer, slot);
        }else {
            menu.addMenuClick(iBuilder, menuClick, slot);
        }
        return menu;
    }
}