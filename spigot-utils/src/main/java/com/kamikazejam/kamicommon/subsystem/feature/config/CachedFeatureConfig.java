package com.kamikazejam.kamicommon.subsystem.feature.config;

import com.kamikazejam.kamicommon.configuration.spigot.ICachedConfig;
import com.kamikazejam.kamicommon.configuration.spigot.KamiConfig;
import com.kamikazejam.kamicommon.subsystem.feature.Feature;
import com.kamikazejam.kamicommon.subsystem.feature.FeatureConfig;
import com.kamikazejam.kamicommon.util.MessageBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@Getter @Setter
@SuppressWarnings("unused")
public abstract class CachedFeatureConfig<F extends Feature> implements ICachedConfig<FeatureConfig> {
    private final @NotNull F feature;
    protected boolean shutdownOnFailure = true;
    private boolean loaded = false;

    public CachedFeatureConfig(@NotNull F feature) {
        this.feature = feature;
    }

    @Override
    public void registerObserver() {
        this.feature.registerConfigObserver(this);
    }

    @Override
    public final void reloadConfig() {
        try {
            this.onConfigLoaded(feature.getConfig());
        } catch (Throwable t) {
            t.printStackTrace();
            if (shutdownOnFailure) {
                Bukkit.getLogger().severe("Failed to load config, shutting down server.");
                Bukkit.getServer().shutdown();
            }
        }
    }

    @ApiStatus.Internal
    @Override
    public final void onConfigLoaded(@NotNull KamiConfig config) {
        loadConfig((FeatureConfig) config);
        this.loaded = true;
    }

    @Override
    public abstract void loadConfig(@NotNull FeatureConfig config);

    @Override
    public void warn(@NotNull String message) {
        feature.warn(message);
    }

    @Override
    public MessageBuilder msg(@NotNull String key) {
        return new MessageBuilder(feature.getConfig(), key);
    }
}
