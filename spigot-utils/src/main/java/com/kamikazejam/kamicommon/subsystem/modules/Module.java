package com.kamikazejam.kamicommon.subsystem.modules;

import com.kamikazejam.kamicommon.KamiPlugin;
import com.kamikazejam.kamicommon.configuration.spigot.KamiConfigExt;
import com.kamikazejam.kamicommon.subsystem.AbstractSubsystem;
import com.kamikazejam.kamicommon.subsystem.SubsystemConfig;
import com.kamikazejam.kamicommon.subsystem.feature.Feature;
import com.kamikazejam.kamicommon.util.Preconditions;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * This class represents a single module registered under your {@link KamiPlugin} plugin.<br>
 * A module is a toggleable subsystem that acts like its own plugin, providing its own functionality and configuration.<br>
 * For a subsystem that cannot be toggled or disabled, see {@link Feature}.<br>
 */
@SuppressWarnings("unused")
public abstract class Module extends AbstractSubsystem<ModuleConfig, Module> {
    public static final @NotNull String MODULES_FOLDER = "modules";

    /**
     * @return Whether this module is enabled by default (generally always true, except in specific situations) <br>
     */
    @SuppressWarnings("SameReturnValue")
    public abstract boolean isEnabledByDefault();

    /**
     * @return The default logging prefix for this subsystem (saved under the KamiPlugin modulesConfig modulePrefix)
     */
    @Override
    public abstract @NotNull String defaultPrefix();

    // -------------------------------------------- //
    // MODULE CONFIG
    // -------------------------------------------- //
    @Override
    public @NotNull File getConfigFileDestination() {
        // Default: /home/container/plugins/<plugin>/modules/<module>.yml
        return new File(this.getPlugin().getDataFolder() + File.separator + MODULES_FOLDER + File.separator + this.getName() + ".yml");
    }

    /**
     * The name of the config file (IN SOURCE CODE) for this Module.<br>
     * <br>
     * By default, it is fetched as {@link KamiPlugin#getModuleYmlPath()}/[name]Module.yml<br>
     * <br>
     * You can override this method, or edit {@link KamiPlugin#getModuleYmlPath()} to change the path resolution.
     */
    public @NotNull String getConfigResourcePath() {
        @NotNull String ymlPath = this.getPlugin().getModuleYmlPath();
        if (ymlPath.endsWith("/")) {
            ymlPath = ymlPath.substring(0, ymlPath.length() - 1);
        }
        return ymlPath + File.separator + this.getName() + "Module.yml";
    }

    @Override
    protected @NotNull ModuleConfig createConfig() {
        @NotNull String configResourcePath = this.getConfigResourcePath();
        Preconditions.checkNotNull(
                SubsystemConfig.getIS(this, configResourcePath),
                "Module YML resource is invalid! ('" + configResourcePath + "') This module config cannot be loaded!"
        );

        return new ModuleConfig(this, configResourcePath);
    }

    @Internal
    public final boolean isEnabledInConfig() {
        // Create a throw-away config so that we don't start any initialization logic
        // if this module does not intend to be enabled
        ModuleConfig config = createConfig();
        boolean enabled = config.getBoolean("enabled", isEnabledByDefault());
        // If the module is enabled in the config, begin initialization
        if (enabled) {
            initializeConfig(config);
        }
        return enabled;
    }

    @Override
    public final @NotNull String getPrefix() {
        KamiConfigExt c = getPlugin().getModulesConfig();
        String prefix = c.getString("modules." + getName() + ".modulePrefix", null);
        if (prefix != null) { return prefix; }

        String def = defaultPrefix();
        c.setString("modules." + getName() + ".modulePrefix", def);
        c.save();
        return def;
    }

    @NotNull
    public File getModuleDataFolder() {
        File dataFolder = getPlugin().getDataFolder();
        File moduleFolder = new File(dataFolder + File.separator + MODULES_FOLDER + File.separator + getName());
        if (!moduleFolder.exists()) {
            boolean created = moduleFolder.mkdirs();
            if (!created) {
                throw new IllegalStateException("Failed to create module data folder: " + moduleFolder.getAbsolutePath());
            }
        }
        return moduleFolder;
    }
}
