package com.kamikazejam.kamicommon.util.components.actions;

import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;

@SuppressWarnings("unused")
public class ClickSuggest extends Click {
    @Getter
    private final String suggestion;

    /**
     * Creates a ClickSuggest object which will only have a clickEvent for running the command
     *  Use .setHoverText() or .setHoverItem() to chain a hoverEvent
     * @param placeholder The placeholder to search strings for
     * @param replacement The text to replace the placeholder with
     * @param suggestion The text/cmd to suggest when clicked
     */
    public ClickSuggest(String placeholder, String replacement, String suggestion) {
        super(placeholder, replacement);
        this.suggestion = suggestion;
    }

    @Override
    public void addClickEvent(BaseComponent component) {
        component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestion));
    }
}

