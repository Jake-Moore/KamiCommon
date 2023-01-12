package com.kamikazejamplugins.kamicommon.util.components.actions.hoveritem;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

class ItemText_1_19_R2 implements ItemText {
    @Override
    public BaseComponent[] getItemText(ItemStack item) {
        net.minecraft.world.item.ItemStack v1_19_R2Stack = CraftItemStack.asNMSCopy(item);
        if (v1_19_R2Stack == null) { return TextComponent.fromLegacyText(""); }
        return new BaseComponent[]{ new TextComponent(v1_19_R2Stack.b(new NBTTagCompound()).toString()) };
    }
}
