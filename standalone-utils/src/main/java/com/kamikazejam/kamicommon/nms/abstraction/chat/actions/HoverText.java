package com.kamikazejam.kamicommon.nms.abstraction.chat.actions;

import com.kamikazejam.kamicommon.util.StringUtil;
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
        this.text = StringUtil.t(text);
    }
}
