package com.kamikazejam.kamicommon.nms.abstraction.hoveritem;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.inventory.ItemStack;

public interface AbstractItemText {
    BaseComponent[] getComponents(ItemStack item);

    String getNbtStringTooltip(ItemStack item);

}
