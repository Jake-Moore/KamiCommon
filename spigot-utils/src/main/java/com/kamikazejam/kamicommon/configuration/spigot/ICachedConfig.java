package com.kamikazejam.kamicommon.configuration.spigot;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

public interface ICachedConfig<T extends KamiConfig> extends ConfigObserver {
    // API
    @Internal
    void loadConfig(@NotNull T config);

    // Implementation
    void registerObserver();
    void reloadConfig();
}
