package com.kamikazejam.kamicommon.nms.hoveritem;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ItemText_1_18_R1 implements ItemText {
    @Override
    public BaseComponent[] getComponents(ItemStack item) {
        net.minecraft.world.item.ItemStack v1_18_R1Stack = CraftItemStack.asNMSCopy(item);
        if (v1_18_R1Stack == null) { return TextComponent.fromLegacyText(""); }
        return new BaseComponent[]{ new TextComponent(v1_18_R1Stack.b(new NBTTagCompound()).toString()) };
    }

    @Override
    public String getNbtStringTooltip(ItemStack item) {
        net.minecraft.world.item.ItemStack v1_18_R1Stack = CraftItemStack.asNMSCopy(item);
        if (v1_18_R1Stack == null) { return ""; }
        return v1_18_R1Stack.b(new NBTTagCompound()).toString();
    }
}
