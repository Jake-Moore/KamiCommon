package com.kamikazejam.kamicommon.nms.item;

import com.kamikazejam.kamicommon.nms.abstraction.item.NmsItemMethods;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

// Verified on 1.20.CB and 1.21
@SuppressWarnings("unused")
public class NmsItemMethods_1_21_CB implements NmsItemMethods {
    @Override
    @SuppressWarnings("deprecation")
    public @NotNull String getI18NItemName(@NotNull ItemStack itemStack) {
        return Objects.requireNonNull(itemStack.getI18NDisplayName());
    }
}
