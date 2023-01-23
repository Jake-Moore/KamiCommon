package com.kamikazejamplugins.kamicommon.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public enum TriState {
    NOT_SET,
    FALSE,
    TRUE;

    TriState() {}

    public static @NotNull TriState byBoolean(final boolean value) {
        return value ? TRUE : FALSE;
    }

    public static @NotNull TriState byBoolean(final @Nullable Boolean value) {
        return value == null ? NOT_SET : byBoolean(value);
    }

    public boolean toBoolean() {
        return this == TRUE;
    }
}
