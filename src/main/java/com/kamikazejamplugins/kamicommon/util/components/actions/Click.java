package com.kamikazejamplugins.kamicommon.util.components.actions;

import com.kamikazejamplugins.kamicommon.util.components.actions.hoveritem.HoverItem;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.inventory.ItemStack;

// ClickEvent.Action.RUN_COMMAND;
// ClickEvent.Action.SUGGEST_COMMAND;
// ClickEvent.Action.OPEN_URL

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class Click extends Action {
    @Getter private Hover hoverAction = null;
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
    public void addHoverEvent(BaseComponent component) {
        if (hoverAction == null) { return; }
        hoverAction.addHoverEvent(component);
    }

    public abstract void addClickEvent(BaseComponent component);
}
