package com.kamikazejam.kamicommon.actions;

import com.kamikazejam.kamicommon.util.LegacyColors;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
@SuppressWarnings({"unused"})
public class HoverText extends Hover {
    private final @NotNull String text;

    /**
     * @param text The text to show when hovering
     */
    HoverText(@NotNull String text) {
        this.text = LegacyColors.t(text);
    }
}
