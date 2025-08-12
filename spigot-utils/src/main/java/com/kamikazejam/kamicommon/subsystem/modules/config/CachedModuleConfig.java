package com.kamikazejam.kamicommon.subsystem.modules.config;

import com.kamikazejam.kamicommon.configuration.spigot.ICachedConfig;
import com.kamikazejam.kamicommon.configuration.spigot.KamiConfig;
import com.kamikazejam.kamicommon.subsystem.modules.Module;
import com.kamikazejam.kamicommon.subsystem.modules.ModuleConfig;
import com.kamikazejam.kamicommon.util.MessageBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@Getter @Setter
@SuppressWarnings("unused")
public abstract class CachedModuleConfig<M extends Module> implements ICachedConfig<ModuleConfig> {
    private final @NotNull M module;
    protected boolean shutdownOnFailure = true;
    private boolean loaded = false;

    public CachedModuleConfig(@NotNull M module) {
        this.module = module;
    }

    @Override
    public void registerObserver() {
        this.module.registerConfigObserver(this);
    }

    @Override
    public final void reloadConfig() {
        try {
            this.onConfigLoaded(module.getConfig());
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
        loadConfig((ModuleConfig) config);
        this.loaded = true;
    }

    @Override
    public abstract void loadConfig(@NotNull ModuleConfig config);

    @Override
    public void warn(@NotNull String message) {
        module.warn(message);
    }

    @Override
    public MessageBuilder msg(@NotNull String key) {
        return new MessageBuilder(module.getConfig(), key);
    }
}
