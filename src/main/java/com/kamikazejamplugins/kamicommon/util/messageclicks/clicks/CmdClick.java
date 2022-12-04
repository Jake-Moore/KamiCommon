package com.kamikazejamplugins.kamicommon.util.messageclicks.clicks;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CmdClick extends Click {
    public final String cmd;

    public CmdClick(String replacement, String text, String hover, String cmd) {
        super(replacement, text, hover);
        this.cmd = cmd;
    }

    @Override
    public void setClickEvent(TextComponent component) {
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
    }
}

