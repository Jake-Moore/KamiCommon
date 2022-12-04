package com.kamikazejamplugins.kamicommon.util.components.actions;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;

// ClickEvent.Action.RUN_COMMAND;
// ClickEvent.Action.SUGGEST_COMMAND;
// ClickEvent.Action.OPEN_URL

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class Click extends Action {
    public Hover hoverAction = null;
    public Click(String placeholder, String replacement) {
        super(placeholder, replacement);
    }

    public Click setHoverText(String hover) {
        this.hoverAction = new HoverText("", "", hover);
        return this;
    }

    public Click setHoverItem(ItemStack itemStack) {
        this.hoverAction = new HoverItem("", "", itemStack);
        return this;
    }

    @Override
    public void addHoverEvent(TextComponent component) {
        if (hoverAction == null) { return; }
        hoverAction.addHoverEvent(component);
    }

    public abstract void addClickEvent(TextComponent component);
}
