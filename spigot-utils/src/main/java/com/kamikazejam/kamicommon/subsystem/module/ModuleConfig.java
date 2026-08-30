package com.kamikazejam.kamicommon.subsystem.module;

import com.kamikazejam.kamicommon.configuration.spigot.KamiConfigExt;
import com.kamikazejam.kamicommon.subsystem.SubsystemConfig;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.yaml.source.ConfigSource;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

/**
 * The configuration of one {@link Module}, defaulted from that module's resource in the plugin jar and
 * written under the plugin's data folder. It is created lazily by {@link Module#createConfig()}, so a
 * module reaches its own with {@link Module#getConfig()} rather than constructing this directly.
 *
 * @see <a href="https://github.com/Jake-Moore/KamiCommon/wiki/v5-Subsystems#config-layout">Subsystem config layout (wiki)</a>
 */
@SuppressWarnings("unused")
public class ModuleConfig extends SubsystemConfig<Module> {

    // Constructor for modules storing configs in Files on the server filesystem
    //   resourcePath is in form: "moduleYmlPath + <module>.yml"
    public ModuleConfig(@NotNull Module module, @NotNull String resourcePath) {
        super(
                Preconditions.checkNotNull(module, "Module cannot be null"),
                Preconditions.checkNotNull(resourcePath, "File name cannot be null")
        );
    }

    // Constructor for modules using ConfigSource (e.g. from a database or other source)
    public ModuleConfig(@NotNull Module module, @NotNull ConfigSource source, @NotNull String resourcePath) {
        super(
                Preconditions.checkNotNull(module, "Module cannot be null"),
                Preconditions.checkNotNull(source, "ConfigSource cannot be null"),
                Preconditions.checkNotNull(resourcePath, "Resource path cannot be null")
        );
    }

    @NotNull
    public Module getModule() {
        return this.getSubsystem();
    }

    @Internal
    public boolean isEnabledInConfig() {
        Module module = this.getModule();
        KamiConfigExt c = module.getPlugin().getModulesConfig();
        String key = module.getModulesConfigKey() + ".enabled";

        // Warn if the module does not have an entry in the config so the plugin author can go add a default in the resource file
        if (!c.contains(key)) {
            // Through the plugin logger, NOT module.getLogger(). The subsystem logger is assigned in
            // AbstractSubsystem#handleEnable, which ModuleManager#registerModule only reaches after
            // this check, so module.getLogger() is null here. The warning meant to tell the author
            // about a missing key instead threw an NPE that registerModule's catch(Throwable)
            // reported as "Can not register the module".
            module.getPlugin().getLogger().warning(
                    "Module '" + module.getName() + "' missing boolean key '" + key + "' in the modules config. Using default: " + module.isEnabledByDefault()
            );
        }

        return c.getBoolean(key, module.isEnabledByDefault());
    }
}
