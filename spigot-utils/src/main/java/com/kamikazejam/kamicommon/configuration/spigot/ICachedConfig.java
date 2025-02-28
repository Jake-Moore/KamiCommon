package com.kamikazejam.kamicommon.configuration.spigot;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.modules.ModuleConfig;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public interface ICachedConfig<T extends KamiConfig> extends ConfigObserver {
    // API
    void loadConfig(@NotNull T config);

    // Implementation
    void registerObserver();
    /**
     * Manual reload method. Call this to have {@link #onConfigLoaded(KamiConfig)} called again & your cache reloaded.<br>
     * This {@link CachedConfig} class as a member of {@link ConfigObserver} is automatically registered to receive all config reloads.<br>
     * As such, calling this method is optional and not required. Any call to the config passed in the constructor, will trigger {@link #loadConfig}
     */
    void reloadConfig();

    // ------------------------------------ //
    // Helper Methods
    // ------------------------------------ //

    default void warn(@NotNull String message) {
        Bukkit.getLogger().warning(message);
    }

    default @NotNull List<XMaterial> loadMaterials(@NotNull ModuleConfig config, @NotNull String key) {
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
}
