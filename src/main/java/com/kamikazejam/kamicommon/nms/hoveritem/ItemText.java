package com.kamikazejam.kamicommon.nms.hoveritem;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.inventory.ItemStack;

public interface ItemText {
    BaseComponent[] getComponents(ItemStack item);

    String getNbtStringTooltip(ItemStack item);
}
