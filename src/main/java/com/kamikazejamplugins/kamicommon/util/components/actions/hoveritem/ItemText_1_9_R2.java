package com.kamikazejamplugins.kamicommon.util.components.actions.hoveritem;

import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_9_R2.NBTTagCompound;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

class ItemText_1_9_R2 implements ItemText {
    @Override
    public TextComponent getItemText(ItemStack item) {
        net.minecraft.server.v1_9_R2.ItemStack v1_9_R2Stack = CraftItemStack.asNMSCopy(item);
        if (v1_9_R2Stack == null) { return new TextComponent(""); }
        return new TextComponent(v1_9_R2Stack.save(new NBTTagCompound()).toString());
    }
}