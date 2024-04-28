package com.kamikazejam.kamicommon.nms.abstraction.chat.actions;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Action extends StandaloneAction {
    public Action(@NotNull String placeholder, @NotNull String replacement) {
        super(placeholder, replacement);
    }

    /**
     * @param item The ItemStack to show when hovering
     */
    public Action setHoverItem(ItemStack item) {
        this.hover = new HoverItem(item);
        return this;
    }
}
