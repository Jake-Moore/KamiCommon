package com.kamikazejam.kamicommon.nms.itemtext;

import com.kamikazejam.kamicommon.nms.abstraction.itemtext.AbstractItemTextPre_1_17;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R2.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ItemText_1_8_R2 implements AbstractItemTextPre_1_17 {
    @Override
    public BaseComponent[] getComponents(ItemStack item) {
        net.minecraft.server.v1_8_R2.ItemStack v1_8_R2Stack = CraftItemStack.asNMSCopy(item);
        if (v1_8_R2Stack == null) { return TextComponent.fromLegacyText(""); }
        return new BaseComponent[]{ new TextComponent(v1_8_R2Stack.save(new NBTTagCompound()).toString()) };
    }

    @Override
    public String getNbtStringTooltip(ItemStack item) {
        net.minecraft.server.v1_8_R2.ItemStack v1_8_R2Stack = CraftItemStack.asNMSCopy(item);
        if (v1_8_R2Stack == null) { return ""; }
        return v1_8_R2Stack.save(new NBTTagCompound()).toString();
    }
}
