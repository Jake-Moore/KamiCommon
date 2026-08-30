package com.kamikazejam.kamicommon.util.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A boolean that can also be absent, for a setting that is explicitly true, explicitly false, or simply not
 * configured. {@link #byBoolean(Boolean)} maps {@code null} to {@link #NOT_SET}, while {@link #toBoolean()}
 * collapses {@link #NOT_SET} back to {@code false}, so compare against the constant directly wherever
 * "unset" and "false" must behave differently.
 */
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
