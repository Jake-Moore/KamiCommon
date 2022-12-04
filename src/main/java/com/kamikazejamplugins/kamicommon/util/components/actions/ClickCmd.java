package com.kamikazejamplugins.kamicommon.util.components.actions;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

@SuppressWarnings("unused")
public class ClickCmd extends Click {
    public final String cmd;

    /**
     * Creates a ClickCmd object which will only have a clickEvent for running the command
     *  Use .setHoverText() or .setHoverItem() to chain a hoverEvent
     * @param placeholder The placeholder to search strings for
     * @param replacement The text to replace the placeholder with
     * @param cmd The cmd to run when clicked
     */
    public ClickCmd(String placeholder, String replacement, String cmd) {
        super(placeholder, replacement);
        this.cmd = cmd;
    }

    @Override
    public void addClickEvent(TextComponent component) {
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
    }
}

