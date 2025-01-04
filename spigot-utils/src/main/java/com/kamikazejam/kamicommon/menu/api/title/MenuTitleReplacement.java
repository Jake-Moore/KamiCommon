package com.kamikazejam.kamicommon.menu.api.title;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class MenuTitleReplacement {
    private final @NotNull CharSequence target;
    private final @NotNull CharSequence replacement;
}
