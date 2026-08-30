package com.kamikazejam.kamicommon.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Some basic preconditions for when the Google preconditions are not available on the classpath
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Preconditions {
    public static @NotNull <T> T checkNotNull(@Nullable T reference) {
        if (reference == null) {
            throw new NullPointerException();
        } else {
            return reference;
        }
    }

    public static @NotNull <T> T checkNotNull(@Nullable T reference, @Nullable Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        } else {
            return reference;
        }
    }

    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgument(boolean expression, @Nullable String errorMessage) {
        if (!expression) {
            throw (errorMessage == null) ? new IllegalArgumentException() : new IllegalArgumentException(errorMessage);
        }
    }
}
