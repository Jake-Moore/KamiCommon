package com.kamikazejamplugins.kamicommon.util.components.actions;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

@SuppressWarnings("unused")
public class ClickUrl extends Click {
    public final String url;

    /**
     * Creates a ClickUrl object which will only have a clickEvent for running the command
     *  Use .setHoverText() or .setHoverItem() to chain a hoverEvent
     * @param placeholder The placeholder to search strings for
     * @param replacement The text to replace the placeholder with
     * @param url The url to prompt when clicked
     */
    public ClickUrl(String placeholder, String replacement, String url) {
        super(placeholder, replacement);
        this.url = url;
    }

    @Override
    public void addClickEvent(TextComponent component) {
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
    }
}

