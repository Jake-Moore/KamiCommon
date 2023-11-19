package com.kamikazejam.kamicommon.util.components.actions;

import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;

// HoverEvent.Action.SHOW_TEXT;
// HoverEvent.Action.SHOW_ITEM;

@SuppressWarnings("unused")
public abstract class Hover extends Action {
    @Getter
    private Click clickAction = null;
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
    public void addClickEvent(BaseComponent component) {
        if (clickAction == null) { return; }
        clickAction.addClickEvent(component);
    }

    public abstract void addHoverEvent(BaseComponent component);
}
