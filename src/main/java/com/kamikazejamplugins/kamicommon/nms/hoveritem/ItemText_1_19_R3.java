package com.kamikazejamplugins.kamicommon.nms.hoveritem;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ItemText_1_19_R3 implements ItemText {
    @Override
    public BaseComponent[] getComponents(ItemStack item) {
        net.minecraft.world.item.ItemStack v1_19_R3Stack = CraftItemStack.asNMSCopy(item);
        if (v1_19_R3Stack == null) { return TextComponent.fromLegacyText(""); }
        return new BaseComponent[]{ new TextComponent(v1_19_R3Stack.b(new NBTTagCompound()).toString()) };
    }
}
