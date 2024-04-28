package com.kamikazejam.kamicommon.nms.chat;

import com.kamikazejam.kamicommon.nms.abstraction.chat.AbstractMessageManager;
import com.kamikazejam.kamicommon.nms.abstraction.chat.actions.*;
import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.chat.MessagePart;
import com.kamikazejam.kamicommon.util.chat.MessageParter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MessageManager_1_17_R1 extends AbstractMessageManager {
    @Override
    public void processAndSend(Player player, String s, boolean translate, Action... actions) {
        if (translate) { s = StringUtil.t(s); }

        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();

        List<MessagePart> messageParts = MessageParter.getMessageParts(s, actions);
        TextComponent component = Component.empty();
        for (MessagePart messagePart : messageParts) {
            TextComponent part = serializer.deserialize(messagePart.getText());

            @Nullable Click click = messagePart.getClick();
            if (click instanceof ClickCmd) {
                part = part.clickEvent(ClickEvent.runCommand(((ClickCmd) click).getCommand()));
            }else if (click instanceof ClickSuggest) {
                part = part.clickEvent(ClickEvent.suggestCommand(((ClickSuggest) click).getSuggestion()));
            }else if (click instanceof ClickUrl) {
                part = part.clickEvent(ClickEvent.openUrl(((ClickUrl) click).getUrl()));
            }

            @Nullable Hover hover = messagePart.getHover();
            if (hover instanceof HoverText) {
                part = part.hoverEvent(HoverEvent.showText(serializer.deserialize(((HoverText) hover).getText())));
            }else if (hover instanceof HoverItem) {
                ItemStack item = ((HoverItem) hover).getItemStack();
                part = part.hoverEvent(item.asHoverEvent());
            }
            component = component.append(part);
        }

        player.sendMessage(component);
    }
}
