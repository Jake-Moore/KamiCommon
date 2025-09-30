package com.kamikazejam.kamicommon.subsystem.module;

import com.kamikazejam.kamicommon.configuration.spigot.KamiConfigExt;
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
    @Override
    public final void addConfigDefaults() {
        Module module = this.getModule();
        KamiConfigExt c = module.getPlugin().getModulesConfig();
        c.addDefault(getModulesConfigKey() + ".enabled", module.isEnabledByDefault());
        c.addDefault(getModulesConfigKey() + ".modulePrefix", module.defaultPrefix().serializeMiniMessage());
        c.save();

        this.save();
        this.reload();
    }

    @Internal
    public boolean isEnabledInConfig() {
        Module module = this.getModule();
        KamiConfigExt c = module.getPlugin().getModulesConfig();
        return c.getBoolean(getModulesConfigKey() + ".enabled", module.isEnabledByDefault());
    }

    private String getModulesConfigKey() {
        return "modules." + this.getModule().getName().replace(" ", "_");
    }
}
