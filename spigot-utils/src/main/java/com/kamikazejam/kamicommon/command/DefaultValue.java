package com.kamikazejam.kamicommon.command;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

/**
 * Encapsulates the default value configuration for a {@link Parameter}.
 */
@Getter
public class DefaultValue<T> {
    private final @Nullable T value;
    private final @Nullable String description;

    public DefaultValue(@Nullable T value, @Nullable String description) {
        this.value = value;
        this.description = description;
    }

    public boolean isDescriptionSet() {
        return this.description != null;
    }

    public static <T> DefaultValue<T> of(@Nullable T value, @Nullable String description) {
        return new DefaultValue<>(value, description);
    }
} 