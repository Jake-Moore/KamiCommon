package com.kamikazejamplugins.kamicommon.nms.hoveritem;

import com.kamikazejamplugins.kamicommon.nms.NmsManager;
import com.kamikazejamplugins.kamicommon.util.components.actions.Hover;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class HoverItem extends Hover {
    @Getter private final @Nullable ItemStack itemStack;

    /**
     * Creates a HoverItem object which will only have a hoverEvent for running the command
     *  Use .setClickCommand() or .setClickSuggestion() to chain a clickEvent
     * @param placeholder The placeholder to search strings for
     * @param replacement The text to replace the placeholder with
     * @param itemStack The itemstack to show when hovering
     */
    public HoverItem(String placeholder, String replacement, @Nullable ItemStack itemStack) {
        super(placeholder, replacement);
        this.itemStack = itemStack;
    }

    @Override
    public void addHoverEvent(BaseComponent component) {
        if (itemStack == null) {
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(" ")));
        }else {
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, getItemText(itemStack)));
        }
    }

    /**
     * Creates a TextComponent that can be used in a HoverEvent from an ItemStack.
     *
     * @param item the ItemStack to be converted to text
     */
    public static BaseComponent[] getItemText(ItemStack item) {
        if (item == null) { return TextComponent.fromLegacyText(""); }
        return NmsManager.getItemText().getComponents(item);
    }
}
