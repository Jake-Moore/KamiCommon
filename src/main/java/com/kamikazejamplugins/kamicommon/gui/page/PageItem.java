package com.kamikazejamplugins.kamicommon.gui.page;

import com.kamikazejamplugins.kamicommon.gui.interfaces.MenuClickPlayer;
import com.kamikazejamplugins.kamicommon.item.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
@Getter @Setter
public class PageItem<T extends Player> {

    public ItemStack itemStack;
    public MenuClickPlayer<T> menuClick;

    public PageItem(ItemStack itemStack, MenuClickPlayer<T> menuClick) {
        this.itemStack = itemStack;
        this.menuClick = menuClick;
    }

    public PageItem(ItemBuilder itemStack, MenuClickPlayer<T> menuClick) {
        this.itemStack = itemStack.toItemStack();
        this.menuClick = menuClick;
    }

}