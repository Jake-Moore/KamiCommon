package com.kamikazejamplugins.kamicommon.util.components.actions.hoveritem;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;

interface ItemText {
    TextComponent getItemText(ItemStack item);
}
