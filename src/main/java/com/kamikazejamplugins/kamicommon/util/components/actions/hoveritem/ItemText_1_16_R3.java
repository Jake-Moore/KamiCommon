package com.kamikazejamplugins.kamicommon.util.components.actions.hoveritem;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

class ItemText_1_16_R3 implements ItemText {
    @Override
    public BaseComponent[] getItemText(ItemStack item) {
        net.minecraft.server.v1_16_R3.ItemStack v1_16_R3Stack = CraftItemStack.asNMSCopy(item);
        if (v1_16_R3Stack == null) { return TextComponent.fromLegacyText(""); }
        return new BaseComponent[]{ new TextComponent(v1_16_R3Stack.save(new NBTTagCompound()).toString()) };
    }
}
