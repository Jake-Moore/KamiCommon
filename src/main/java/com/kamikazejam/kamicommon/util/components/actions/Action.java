package com.kamikazejam.kamicommon.util.components.actions;

import com.kamikazejam.kamicommon.util.StringUtil;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;

@Getter
public abstract class Action {
    private final String placeholder;
    private final String replacement;

    public Action(String placeholder, String replacement) {
        this.placeholder = placeholder;
        this.replacement = StringUtil.t(replacement);
    }

    public abstract void addClickEvent(BaseComponent component);

    public abstract void addHoverEvent(BaseComponent component);
}
