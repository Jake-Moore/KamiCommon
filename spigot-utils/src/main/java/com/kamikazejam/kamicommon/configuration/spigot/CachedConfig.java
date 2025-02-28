package com.kamikazejam.kamicommon.configuration.spigot;

import com.kamikazejam.kamicommon.util.MessageBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@Getter @Setter
@SuppressWarnings("unused")
public abstract class CachedConfig<T extends KamiConfig> implements ICachedConfig<T> {
    protected boolean shutdownOnFailure = true;
    private boolean loaded = false;

    private final @NotNull T config;
    public CachedConfig(@NotNull T config) {
        this.config = config;
    }

    @Override
    public void registerObserver() {
        // Register this class as an observer of the config
        //  so the onConfigLoaded method is called automatically
        config.registerObserver(this);
    }

    @Override
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
        loadConfig((T) config);
        this.loaded = true;
    }

    @Override
    public abstract void loadConfig(@NotNull T config);

    // ------------------------------------ //
    // Helper Methods
    // ------------------------------------ //
    /**
     * Constructs a new {@link MessageBuilder} with the given key and this config.
     */
    @NotNull
    public MessageBuilder msg(@NotNull String key) {
        return new MessageBuilder(config, key);
    }
}
