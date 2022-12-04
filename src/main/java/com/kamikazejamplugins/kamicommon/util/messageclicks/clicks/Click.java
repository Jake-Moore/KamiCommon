package com.kamikazejamplugins.kamicommon.util.messageclicks.clicks;

import com.kamikazejamplugins.kamicommon.util.StringUtil;
import net.md_5.bungee.api.chat.TextComponent;

public abstract class Click {
    public final String replacement;
    public final String text;
    public final String hover;

    public Click(String replacement, String text, String hover) {
        this.replacement = replacement;
        this.text = StringUtil.t(text);
        this.hover = StringUtil.t(hover);
    }

    public abstract void setClickEvent(TextComponent component);
}
