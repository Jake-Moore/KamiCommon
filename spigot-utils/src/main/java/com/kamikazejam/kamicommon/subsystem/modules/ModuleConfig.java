package com.kamikazejam.kamicommon.subsystem.modules;

import com.kamikazejam.kamicommon.configuration.spigot.KamiConfigExt;
import com.kamikazejam.kamicommon.subsystem.SubsystemConfig;
import com.kamikazejam.kamicommon.util.Preconditions;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ModuleConfig extends SubsystemConfig<Module> {
    public static final @NotNull String MODULES_FOLDER = "modules";

    // Filename is in form: "moduleYmlPath + <module>.yml"
    public ModuleConfig(@NotNull Module module, @NotNull String fileName) {
        super(
                Preconditions.checkNotNull(module, "Module cannot be null"),
                MODULES_FOLDER,
                Preconditions.checkNotNull(fileName, "File name cannot be null")
        );
    }

    @NotNull
    public Module getModule() {
        return this.getSubsystem();
    }

    @Override
    public final void addConfigDefaults() {
        Module module = this.getModule();
        KamiConfigExt c = module.getPlugin().getModulesConfig();
        String name = module.getName().replace(" ", "_");
        c.addDefault("modules." + name + ".enabled", true);
        c.addDefault("modules." + name + ".modulePrefix", module.defaultPrefix());
        c.save();

        this.save();
        this.reload();
    }
}
