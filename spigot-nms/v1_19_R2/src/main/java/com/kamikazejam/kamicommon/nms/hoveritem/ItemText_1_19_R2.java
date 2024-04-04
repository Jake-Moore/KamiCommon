package com.kamikazejam.kamicommon.nms.hoveritem;

import com.kamikazejam.kamicommon.nms.abstraction.hoveritem.AbstractItemText;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation") // Fine for now, not removed yet :)

public class ItemText_1_19_R2 implements AbstractItemText {
    @Override
    public BaseComponent[] getComponents(ItemStack item) {
        net.minecraft.world.item.ItemStack v1_19_R2Stack = CraftItemStack.asNMSCopy(item);
        if (v1_19_R2Stack == null) { return TextComponent.fromLegacyText(""); }
        return new BaseComponent[]{ new TextComponent(v1_19_R2Stack.save(new CompoundTag()).toString()) };
    }

    @Override
    public String getNbtStringTooltip(ItemStack item) {
        net.minecraft.world.item.ItemStack v1_19_R2Stack = CraftItemStack.asNMSCopy(item);
        if (v1_19_R2Stack == null) { return ""; }
        return v1_19_R2Stack.save(new CompoundTag()).toString();
    }
}
