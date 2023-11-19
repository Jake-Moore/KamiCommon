package com.kamikazejam.kamicommon.nms.hoveritem;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ItemText_1_8_R3 implements ItemText {
    @Override
    public BaseComponent[] getComponents(ItemStack item) {
        net.minecraft.server.v1_8_R3.ItemStack v1_8_R3Stack = CraftItemStack.asNMSCopy(item);
        if (v1_8_R3Stack == null) { return TextComponent.fromLegacyText(""); }
        return new BaseComponent[]{ new TextComponent(v1_8_R3Stack.save(new NBTTagCompound()).toString()) };
    }
}
