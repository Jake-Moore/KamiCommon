package com.kamikazejam.kamicommon.nms.item;

import com.kamikazejam.kamicommon.nms.abstraction.item.NmsItemMethods;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

// Verified on 1.20.CB and 1.21
@SuppressWarnings("unused")
public class NmsItemMethods_1_21_CB implements NmsItemMethods {
    @Override
    public @NotNull String getI18NItemKey(@NotNull ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
        return nmsCopy.getItem().getName(nmsCopy).getString();
    }
}
