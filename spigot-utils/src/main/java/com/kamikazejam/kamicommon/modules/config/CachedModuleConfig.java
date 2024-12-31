package com.kamikazejam.kamicommon.modules.config;

import com.kamikazejam.kamicommon.configuration.spigot.CachedConfig;
import com.kamikazejam.kamicommon.modules.Module;
import com.kamikazejam.kamicommon.modules.ModuleConfig;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
@SuppressWarnings("unused")
public abstract class CachedModuleConfig<M extends Module> extends CachedConfig<ModuleConfig> {
    private final @NotNull M module;
    public CachedModuleConfig(@NotNull M module) {
        super(module.getConfig());
        this.module = module;
    }

    @Override
    public final void warn(@NotNull String message) {
        module.warn(message);
    }
}
