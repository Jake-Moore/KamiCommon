package com.kamikazejam.kamicommon.nms.abstraction.itemtext;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public interface AbstractItemTextPre_1_17 {
    BaseComponent[] getComponents(ItemStack item);

    String getNbtStringTooltip(ItemStack item);
}
