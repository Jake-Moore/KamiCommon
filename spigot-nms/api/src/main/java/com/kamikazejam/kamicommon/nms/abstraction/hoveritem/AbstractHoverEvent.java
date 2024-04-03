package com.kamikazejam.kamicommon.nms.abstraction.hoveritem;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractHoverEvent {
    public abstract @NotNull HoverEvent createHoverEvent(@NotNull AbstractItemText itemText, @NotNull BaseComponent component, @NotNull ItemStack item);
    public abstract @NotNull HoverEvent createHoverEvent(@NotNull AbstractItemText itemText, @NotNull BaseComponent component, @NotNull String text);
}
