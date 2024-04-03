package com.kamikazejam.kamicommon.nms.hoveritem;

import com.kamikazejam.kamicommon.nms.abstraction.hoveritem.AbstractHoverEvent;
import com.kamikazejam.kamicommon.nms.abstraction.hoveritem.AbstractItemText;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HoverEvent_1_8_R1 extends AbstractHoverEvent {
    @Override
    public @NotNull HoverEvent createHoverEvent(@NotNull AbstractItemText itemText, @NotNull BaseComponent component, @NotNull String text) {
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(text));
    }

    @Override
    public @NotNull HoverEvent createHoverEvent(@NotNull AbstractItemText itemText, @NotNull BaseComponent component, @NotNull ItemStack item) {
        return new HoverEvent(HoverEvent.Action.SHOW_ITEM, itemText.getComponents(item));
    }
}
