package com.kamikazejamplugins.kamicommon.util.components.actions.hoveritem;

import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

class ItemText_1_16_R1 implements ItemText {
    @Override
    public TextComponent getItemText(ItemStack item) {
        net.minecraft.server.v1_16_R1.ItemStack v1_16_R1Stack = CraftItemStack.asNMSCopy(item);
        if (v1_16_R1Stack == null) { return new TextComponent(""); }
        return new TextComponent(v1_16_R1Stack.save(new NBTTagCompound()).toString());
    }
}
