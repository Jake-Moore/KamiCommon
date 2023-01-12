package com.kamikazejamplugins.kamicommon.util.components.actions;

import com.kamikazejamplugins.kamicommon.util.StringUtil;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;

public abstract class Action {
    @Getter private final String placeholder;
    @Getter private final String replacement;

    public Action(String placeholder, String replacement) {
        this.placeholder = placeholder;
        this.replacement = StringUtil.t(replacement);
    }

    public abstract void addClickEvent(BaseComponent component);

    public abstract void addHoverEvent(BaseComponent component);
}
