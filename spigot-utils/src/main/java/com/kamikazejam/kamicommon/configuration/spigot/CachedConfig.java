package com.kamikazejam.kamicommon.configuration.spigot;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.configuration.observe.ConfigObserver;
import com.kamikazejam.kamicommon.util.MessageBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Abstract base class for configuration caches that automatically reload when the underlying config changes.
 * <p>
 * This class implements the observer pattern to listen for config changes and provides a framework for
 * caching configuration data in memory. Subclasses should implement {@link #loadCache(KamiConfig)} to
 * define how their specific configuration data should be cached.
 * <p>
 * The class still requires registration via {@link #register()} to start receiving config updates from the backing config.
 *
 * @param <T> the type of {@link KamiConfig} this cached config operates on
 */
@Getter @Setter
@SuppressWarnings("unused")
public abstract class CachedConfig<T extends KamiConfig> implements ConfigObserver<KamiConfig> {
    /**
     * Whether the server should shut down if loading the config fails.
     * Defaults to {@code true}.
     */
    protected boolean shutdownOnFailure = true;

    /**
     * Whether this config has been successfully loaded at least once.
     */
    private boolean loaded = false;

    /**
     * The underlying {@link KamiConfig} instance that this cache observes.
     */
    private final @NotNull T config;

    /**
     * @param config the configuration instance to cache data from
     */
    public CachedConfig(@NotNull T config) {
        this.config = config;
    }

    /**
     * Registers this cached config as an observer of its backing config.<br>
     * Shortcut for calling {@link KamiConfig#registerConfigObserver(ConfigObserver)} on the backing config.<br>
     * i.e. {@code this.config.registerConfigObserver(this); }
     */
    public void register() {
        // Register this class as an observer of the config
        //  so the onConfigLoaded method is called automatically
        config.registerConfigObserver(this);
    }

    /**
     * Manual reload method. Call this to have {@link #onConfigLoaded(KamiConfig)} called again and your cache reloaded.<br>
     * This method DOES NOT reload the underlying config, it simply calls {@link #onConfigLoaded(KamiConfig)}, triggering your caching logic.
     * <p>
     * This {@link CachedConfig} class as a member of {@link ConfigObserver} is automatically registered to receive all config reloads.<br>
     * As such, calling this method is optional and not required. Calls to {@link T#reload()} will automatically trigger {@link #onConfigLoaded(KamiConfig)} to be called.
     */
    public final void reloadConfig() {
        try {
            this.onConfigLoaded(this.config);
        }catch (Throwable t) {
            t.printStackTrace();
            if (shutdownOnFailure) {
                Bukkit.getLogger().severe("Failed to load config, shutting down server.");
                Bukkit.getServer().shutdown();
            }
        }
    }

    @ApiStatus.Internal
    @Override
    @SuppressWarnings("unchecked")
    public final void onConfigLoaded(@NotNull KamiConfig config) {
        loadCache((T) config);
        this.loaded = true;
    }

    /**
     * Should load configuration data into cache from the provided config instance.
     * <p>
     * This method is called automatically whenever the underlying configuration changes,
     * and should be implemented to read and cache all necessary configuration data
     * in memory for optimal performance.
     *
     * @param config the configuration instance to load data from
     */
    public abstract void loadCache(@NotNull T config);

    /**
     * Constructs a new {@link MessageBuilder} with the provided key, from this config.
     */
    @NotNull
    public MessageBuilder msg(@NotNull String key) {
        return new MessageBuilder(config, key);
    }

    // ------------------------------------ //
    // Helper Methods
    // ------------------------------------ //

    /**
     * Loads a list of XMaterials from a configuration string list, filtering out invalid materials.
     * <p>
     * This method reads a string list from the config, attempts to match each string to an {@link XMaterial},
     * and logs warnings for any invalid materials found. Only valid materials are included in the result.
     *
     * @param key    the configuration key containing the material list
     * @return a list of valid XMaterials found in the configuration
     */
    public @NotNull List<XMaterial> loadMaterials(@NotNull String key) {
        return config.getStringList(key).stream()
                .map(XMaterial::matchXMaterial)
                .filter(mat -> {
                    boolean present = mat.isPresent();
                    if (!present) { warn("Invalid material in '" + key + "': " + mat); }
                    return present;
                })
                .map(Optional::get)
                .toList();
    }

    // ------------------------------------ //
    // Internal Methods
    // ------------------------------------ //

    private void warn(@NotNull String message) {
        Bukkit.getLogger().warning(message);
    }
}
