package com.kamikazejam.kamicommon.subsystem.module;

import com.kamikazejam.kamicommon.configuration.spigot.KamiConfigExt;
import com.kamikazejam.kamicommon.subsystem.SubsystemConfig;
import com.kamikazejam.kamicommon.util.Preconditions;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ModuleConfig extends SubsystemConfig<Module> {

    // resourcePath is in form: "moduleYmlPath + <module>.yml"
    public ModuleConfig(@NotNull Module module, @NotNull String resourcePath) {
        super(
                Preconditions.checkNotNull(module, "Module cannot be null"),
                Preconditions.checkNotNull(resourcePath, "File name cannot be null")
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
        c.addDefault(getModulesConfigKey() + ".modulePrefix", module.defaultPrefix());
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
