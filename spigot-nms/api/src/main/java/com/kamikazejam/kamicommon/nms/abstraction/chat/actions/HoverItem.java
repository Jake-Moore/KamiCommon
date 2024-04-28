package com.kamikazejam.kamicommon.nms.abstraction.chat.actions;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
@SuppressWarnings({"unused"})
public class HoverItem extends Hover {
    private final @NotNull ItemStack itemStack;

    /**
     * @param itemStack The ItemStack to show when hovering
     */
    HoverItem(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }
}