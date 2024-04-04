package com.kamikazejam.kamicommon.nms.hoveritem;

import com.kamikazejam.kamicommon.nms.abstraction.hoveritem.AbstractItemText;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation") // Fine for now, not removed yet :)
public class ItemText_1_19_R3 implements AbstractItemText {
    @Override
    public BaseComponent[] getComponents(ItemStack item) {
        net.minecraft.world.item.ItemStack v1_19_R3Stack = CraftItemStack.asNMSCopy(item);
        if (v1_19_R3Stack == null) { return TextComponent.fromLegacyText(""); }
        return new BaseComponent[]{ new TextComponent(v1_19_R3Stack.save(new CompoundTag()).toString()) };
    }

    @Override
    public String getNbtStringTooltip(ItemStack item) {
        net.minecraft.world.item.ItemStack v1_19_R3Stack = CraftItemStack.asNMSCopy(item);
        if (v1_19_R3Stack == null) { return ""; }
        return v1_19_R3Stack.save(new CompoundTag()).toString();
    }
}
