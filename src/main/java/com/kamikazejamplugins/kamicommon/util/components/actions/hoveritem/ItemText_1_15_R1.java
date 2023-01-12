package com.kamikazejamplugins.kamicommon.util.components.actions.hoveritem;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

class ItemText_1_15_R1 implements ItemText {
    @Override
    public BaseComponent[] getItemText(ItemStack item) {
        net.minecraft.server.v1_15_R1.ItemStack v1_15_R1Stack = CraftItemStack.asNMSCopy(item);
        if (v1_15_R1Stack == null) { return TextComponent.fromLegacyText(""); }
        return new BaseComponent[]{ new TextComponent(v1_15_R1Stack.save(new NBTTagCompound()).toString()) };
    }
}
