package com.kamikazejam.kamicommon.nms.item;

import com.kamikazejam.kamicommon.nms.abstraction.item.NmsItemMethods;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class NmsItemMethods_1_8_R3 implements NmsItemMethods {
    @Override
    public @NotNull String getI18NItemKey(@NotNull ItemStack itemStack) {
        net.minecraft.server.v1_8_R3.ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
        return nmsCopy.getItem().e_(nmsCopy);
    }
}
