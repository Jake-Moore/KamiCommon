package com.kamikazejamplugins.kamicommon.util.components.actions;

import com.kamikazejamplugins.kamicommon.util.StringUtil;
import net.md_5.bungee.api.chat.TextComponent;

public abstract class Action {
    public final String placeholder;
    public final String replacement;

    public Action(String placeholder, String replacement) {
        this.placeholder = placeholder;
        this.replacement = StringUtil.t(replacement);
    }

    public abstract void addClickEvent(TextComponent component);

    public abstract void addHoverEvent(TextComponent component);
}
