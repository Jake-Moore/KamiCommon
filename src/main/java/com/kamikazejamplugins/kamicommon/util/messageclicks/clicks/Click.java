package com.kamikazejamplugins.kamicommon.util.messageclicks.clicks;

import net.md_5.bungee.api.chat.TextComponent;

public abstract class Click {
    public final String replacement;
    public final String text;
    public final String hover;

    public Click(String replacement, String text, String hover) {
        this.replacement = replacement;
        this.text = text;
        this.hover = hover;
    }

    public abstract void setClickEvent(TextComponent component);
}
