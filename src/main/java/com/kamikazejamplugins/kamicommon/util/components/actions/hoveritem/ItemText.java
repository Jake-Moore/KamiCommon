package com.kamikazejamplugins.kamicommon.util.components.actions.hoveritem;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.inventory.ItemStack;

interface ItemText {
    BaseComponent[] getItemText(ItemStack item);
}
