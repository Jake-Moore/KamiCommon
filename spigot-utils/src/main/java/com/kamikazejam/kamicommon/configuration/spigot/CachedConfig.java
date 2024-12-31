package com.kamikazejam.kamicommon.configuration.spigot;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.modules.ModuleConfig;
import com.kamikazejam.kamicommon.util.MessageBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@Getter @Setter
@SuppressWarnings("unused")
public abstract class CachedConfig<T extends KamiConfig> implements ConfigObserver {
    protected boolean shutdownOnFailure = true;
    private boolean loaded = false;

    private final @NotNull T config;
    public CachedConfig(@NotNull T config) {
        this.config = config;
        // Register this class as an observer of the config
        //  so the onConfigLoaded method is called automatically
        config.registerObserver(this);
    }

    /**
     * Manual reload method. Call this to have {@link #onConfigLoaded(KamiConfig)} called again & your cache reloaded.<br>
     * This {@link CachedConfig} class as a member of {@link ConfigObserver} is automatically registered to receive all config reloads.<br>
     * As such, calling this method is optional and not required. Any call to the config passed in the constructor, will trigger {@link #loadConfig}
     */
    public final void reloadConfig() {
        try {
            this.onConfigLoaded(this.config);
            this.loaded = true;
        }catch (Throwable t) {
            t.printStackTrace();
            if (shutdownOnFailure) {
                Bukkit.getLogger().severe("Failed to load config, shutting down server.");
                Bukkit.getServer().shutdown();
            }
        }
    }

    @ApiStatus.Internal
    @SuppressWarnings("unchecked")
    public final void onConfigLoaded(@NotNull KamiConfig config) {
        loadConfig((T) config);
    }

    protected abstract void loadConfig(@NotNull T config);

    // ------------------------------------ //
    // Helper Methods
    // ------------------------------------ //

    public void warn(@NotNull String message) {
        Bukkit.getLogger().warning(message);
    }

    public final @NotNull List<XMaterial> loadMaterials(@NotNull ModuleConfig config, @NotNull String key) {
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

    /**
     * Constructs a new {@link MessageBuilder} with the given key and this config.
     */
    @NotNull
    public MessageBuilder msg(@NotNull String key) {
        return new MessageBuilder(config, key);
    }
}
