package com.kamikazejam.kamicommon.util.components.actions;

import com.kamikazejam.kamicommon.util.StringUtil;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;

@Getter
@SuppressWarnings({"unused"})
public class HoverText extends Hover {
    private final @NotNull String text;

    /**
     * Creates a HoverText object which will only have a hoverEvent for running the command
     *  Use .setClickCommand() or .setClickSuggestion() to chain a clickEvent
     * @param placeholder The placeholder to search strings for
     * @param replacement The text to replace the placeholder with
     * @param text The text to show when hovering
     */
    public HoverText(@NotNull String placeholder, @NotNull String replacement, @NotNull String text) {
        super(placeholder, replacement);
        this.text = StringUtil.t(text);
    }

    @Override
    public void addHoverEvent(BaseComponent component) {
        this.addHoverText(component, text);
    }
}
