package com.kamikazejam.kamicommon.configuration.spigot;

import org.jetbrains.annotations.NotNull;

public interface ConfigObserver {
    default void onConfigLoaded(@NotNull KamiConfig config) {}
}
