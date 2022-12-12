package com.kamikazejamplugins.kamicommon.util.components.actions.hoveritem;

import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

class ItemText_1_18_R2 implements ItemText {
    @Override
    public TextComponent getItemText(ItemStack item) {
        net.minecraft.world.item.ItemStack v1_18_R2Stack = CraftItemStack.asNMSCopy(item);
        if (v1_18_R2Stack == null) { return new TextComponent(""); }
        return new TextComponent(v1_18_R2Stack.b(new NBTTagCompound()).toString());
    }
}
