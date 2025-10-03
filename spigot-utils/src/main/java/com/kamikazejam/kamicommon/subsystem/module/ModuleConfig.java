package com.kamikazejam.kamicommon.subsystem.module;

import com.kamikazejam.kamicommon.configuration.spigot.KamiConfigExt;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.subsystem.SubsystemConfig;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.yaml.source.ConfigSource;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

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
        String key = getModulesConfigKey() + ".enabled";

        // Warn if the module does not have an entry in the config so the plugin author can go add a default in the resource file
        if (!c.contains(key)) {
            module.getLogger().warn(NmsAPI.getVersionedComponentSerializer().fromPlainText(
                    "Module '" + module.getName() + "' missing boolean key '" + key + "' in the modules config. Using default: " + module.isEnabledByDefault()
            ));
        }

        return c.getBoolean(key, module.isEnabledByDefault());
    }

    private String getModulesConfigKey() {
        return "modules." + this.getModule().getName().replace(" ", "_");
    }
}
