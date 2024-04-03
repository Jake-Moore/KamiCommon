package com.kamikazejam.kamicommon.nms.hoveritem;

import com.kamikazejam.kamicommon.nms.abstraction.hoveritem.AbstractHoverEvent;
import com.kamikazejam.kamicommon.nms.abstraction.hoveritem.AbstractItemText;
import com.kamikazejam.kamicommon.util.StringUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HoverEvent_1_16_R2 extends AbstractHoverEvent {
    @Override
    public @NotNull HoverEvent createHoverEvent(@NotNull AbstractItemText itemText, @NotNull BaseComponent component, @NotNull String text) {
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(StringUtil.t(text)));
    }

    @Override
    public @NotNull HoverEvent createHoverEvent(@NotNull AbstractItemText itemText, @NotNull BaseComponent component, @NotNull ItemStack itemStack) {
        String nbtToolTip = itemText.getNbtStringTooltip(itemStack);
        String id = itemStack.getType().getKey().toString();
        Item item = new Item(id, itemStack.getAmount(), ItemTag.ofNbt(nbtToolTip));
        return new HoverEvent(HoverEvent.Action.SHOW_ITEM, item);
    }
}
