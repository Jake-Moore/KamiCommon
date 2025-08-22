package com.kamikazejam.kamicommon.configuration.spigot.observe;

import org.jetbrains.annotations.NotNull;

/**
 * Interface for classes that accept {@link ConfigObserver} registrations.<br>
 * <br>
 * See {@link #registerConfigObserver(ConfigObserver)}.
 */
public interface ObservableConfig {
    /**
     * Registers an observer to this config (if not already registered) <br>
     * Refer to the {@link ConfigObserver} docs for information on its lifecycle.
     * @return If the observer was successfully registered from this call (false if already registered)
     */
    boolean registerConfigObserver(@NotNull ConfigObserver observer);

    /**
     * Unregisters an observer from this config
     */
    void unregisterConfigObserver(@NotNull ConfigObserver observer);
}