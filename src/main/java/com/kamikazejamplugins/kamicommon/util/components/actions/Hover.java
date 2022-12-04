package com.kamikazejamplugins.kamicommon.util.components.actions;

import net.md_5.bungee.api.chat.TextComponent;

// HoverEvent.Action.SHOW_TEXT;
// HoverEvent.Action.SHOW_ITEM;

@SuppressWarnings("unused")
public abstract class Hover extends Action {
    public Click clickAction = null;
    public Hover(String placeholder, String replacement) {
        super(placeholder, replacement);
    }

    public Hover setClickCommand(String cmd) {
        this.clickAction = new ClickCmd("", "", cmd);
        return this;
    }

    public Hover setClickSuggestion(String suggestion) {
        this.clickAction = new ClickSuggest("", "", suggestion);
        return this;
    }

    public Hover setClickUrl(String url) {
        this.clickAction = new ClickUrl("", "", url);
        return this;
    }

    @Override
    public void addClickEvent(TextComponent component) {
        if (clickAction == null) { return; }
        clickAction.addClickEvent(component);
    }

    public abstract void addHoverEvent(TextComponent component);
}
