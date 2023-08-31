package com.kamikazejamplugins.kamicommon.gui.page;

import com.kamikazejamplugins.kamicommon.gui.interfaces.MenuClickPlayer;
import com.kamikazejamplugins.kamicommon.item.IBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
@Getter @Setter
public class PageItem {

    public ItemStack itemStack;
    public MenuClickPlayer menuClick;

    public PageItem(ItemStack itemStack, MenuClickPlayer menuClick) {
        this.itemStack = itemStack;
        this.menuClick = menuClick;
    }

    public PageItem(IBuilder itemStack, MenuClickPlayer menuClick) {
        this.itemStack = itemStack.toItemStack();
        this.menuClick = menuClick;
    }

}