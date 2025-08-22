package com.kamikazejam.kamicommon.configuration.spigot.observe;

import com.kamikazejam.kamicommon.configuration.spigot.KamiConfig;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for classes that want to be notified when a config is updated.<br>
 * <br>
 * You should register this observer with a {@link ObservableConfig} class.
 * Then the {@link #onConfigLoaded(KamiConfig)} method will be called each time the config is updated.<br>
 * <br>
 * Observed Config Updates Include:<br>
 * - Initial load of config data<br>
 * - Each time the config is reloaded<br>
 * <br>
 * If this observer is registered after the initial load, it will be called immediately to receive those values.<br>
 * <br>
 * See {@link KamiConfig#registerObserver(ConfigObserver)} for more information.
 */
public interface ConfigObserver {
    /**
     * Called each time the backing config is loaded or reloaded.
     * @param config The updated config object for consumption by this method.
     */
    void onConfigLoaded(@NotNull KamiConfig config);
}
