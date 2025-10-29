package com.kamikazejam.kamicommon.configuration.observe;

import com.kamikazejam.kamicommon.configuration.standalone.AbstractConfig;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for classes that accept {@link ConfigObserver} registrations.<br>
 * <br>
 * See {@link #registerConfigObserver(ConfigObserver)}.
 */
public interface ObservableConfig<T extends AbstractConfig<?>> {
    /**
     * Registers an observer to this config (if not already registered) <br>
     * Refer to the {@link ConfigObserver} docs for information on its lifecycle.
     * @return If the observer was successfully registered from this call (false if already registered)
     */
    boolean registerConfigObserver(@NotNull ConfigObserver<T> observer);

    /**
     * Unregisters an observer from this config
     */
    void unregisterConfigObserver(@NotNull ConfigObserver<T> observer);

    /**
     * Unregisters ALL observers from this config.<br>
     * Intended for shutdown logic, but can be used at any time.
     */
    void unregisterConfigObservers();

    /**
     * Reload the backing config for this observable, notifying all registered observers of the change.
     */
    void reloadObservableConfig();
}