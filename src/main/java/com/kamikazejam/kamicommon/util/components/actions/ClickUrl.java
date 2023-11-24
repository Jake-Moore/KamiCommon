package com.kamikazejam.kamicommon.util.components.actions;

import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;

@Getter
@SuppressWarnings("unused")
public class ClickUrl extends Click {
    private final String url;

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
    public void addClickEvent(BaseComponent component) {
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
    }
}

