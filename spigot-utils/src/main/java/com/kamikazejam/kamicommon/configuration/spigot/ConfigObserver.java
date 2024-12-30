package com.kamikazejam.kamicommon.configuration.spigot;

import org.jetbrains.annotations.NotNull;

/**
 * Interface for classes that want to be notified when a config is reloaded.<br>
 * This interface requires one method to be fulfilled, and then after registering the observer, the method will be called when the config is reloaded.<br>
 * The interface method is also called immediately on registration with the config.<br>
 * See {@link KamiConfig#registerObserver(ConfigObserver)} for more information.
 */
public interface ConfigObserver {
    void onConfigLoaded(@NotNull KamiConfig config);
}
