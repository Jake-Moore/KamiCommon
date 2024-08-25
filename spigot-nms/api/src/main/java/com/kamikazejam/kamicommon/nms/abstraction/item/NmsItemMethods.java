package com.kamikazejam.kamicommon.nms.abstraction.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface NmsItemMethods {
    /**
     * @return The {@link org.bukkit.entity.Item} internal key name
     */
    @NotNull
    String getI18NItemName(@NotNull ItemStack item);
}
