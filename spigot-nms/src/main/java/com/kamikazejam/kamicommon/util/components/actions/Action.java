package com.kamikazejam.kamicommon.util.components.actions;

import com.kamikazejam.kamicommon.util.StringUtil;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class Action {
    private final @NotNull String placeholder;
    private final @NotNull String replacement;

    public Action(@NotNull String placeholder, @NotNull String replacement) {
        this.placeholder = placeholder;
        this.replacement = StringUtil.t(replacement);
    }
}
