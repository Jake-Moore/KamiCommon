package com.kamikazejam.kamicommon.util.components.actions;

import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;

@SuppressWarnings("unused")
public class ClickCmd extends Click {
    @Getter
    private final String cmd;

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
    public void addClickEvent(BaseComponent component) {
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
    }
}

