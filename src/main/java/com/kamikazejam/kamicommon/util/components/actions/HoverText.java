package com.kamikazejam.kamicommon.util.components.actions;

import com.kamikazejam.kamicommon.util.StringUtil;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

@SuppressWarnings("unused")
public class HoverText extends Hover {
    @Getter
    private final String text;

    /**
     * Creates a HoverText object which will only have a hoverEvent for running the command
     *  Use .setClickCommand() or .setClickSuggestion() to chain a clickEvent
     * @param placeholder The placeholder to search strings for
     * @param replacement The text to replace the placeholder with
     * @param text The text to show when hovering
     */
    public HoverText(String placeholder, String replacement, String text) {
        super(placeholder, replacement);
        this.text = StringUtil.t(text);
    }

    @Override
    public void addHoverEvent(BaseComponent component) {
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(text)));
    }
}
