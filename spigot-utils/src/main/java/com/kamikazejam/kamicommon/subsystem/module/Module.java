package com.kamikazejam.kamicommon.subsystem.module;

import com.kamikazejam.kamicommon.KamiPlugin;
import com.kamikazejam.kamicommon.configuration.spigot.KamiConfigExt;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.subsystem.AbstractSubsystem;
import com.kamikazejam.kamicommon.subsystem.SubsystemConfig;
import com.kamikazejam.kamicommon.subsystem.feature.Feature;
import com.kamikazejam.kamicommon.util.ColoredStringParser;
import com.kamikazejam.kamicommon.util.Preconditions;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.ApiStatus.OverrideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public abstract @NotNull VersionedComponent defaultPrefix();

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
        @Nullable String rawYmlPath = this.getPlugin().getModuleYmlPath();
        // KamiPlugin#getModuleYmlPath() returns null by default, and this method is @NotNull, so
        // dereferencing it produced a bare NullPointerException that ModuleManager#registerModule
        // then reported only as "Can not register the module". v4 failed here with a Preconditions
        // message; this restores one that names the method the plugin has to override.
        @NotNull String ymlPath = Preconditions.checkNotNull(
                rawYmlPath,
                "Module '" + this.getName() + "' cannot resolve its config resource: "
                        + this.getPlugin().getName() + " does not override KamiPlugin#getModuleYmlPath(), "
                        + "which returns null by default. Override it to return the jar subpackage "
                        + "holding your module yml resources, or override Module#getConfigResourcePath()."
        );
        if (ymlPath.endsWith("/")) {
            return ymlPath + this.getName() + "Module.yml";
        } else {
            return ymlPath + "/" + this.getName() + "Module.yml";
        }
    }

    @OverrideOnly
    @Override
    public @NotNull ModuleConfig createConfig() {
        @NotNull String configResourcePath = this.getConfigResourcePath();
        // Double check we can obtain the resource stream (may throw)
        SubsystemConfig.getIS(this, configResourcePath);

        return new ModuleConfig(this, configResourcePath);
    }

    @Internal
    public final boolean isEnabledInConfig() {
        // Construct the config ONCE. Every construction loads the module's yml and force-saves
        // it, so the second call here was a redundant round trip to disk for every module on every
        // startup, and it discarded the instance the enabled check had just been read from.
        ModuleConfig config = createConfig();
        boolean enabled = config.isEnabledInConfig();

        // Only adopt the config as this module's own if it is actually going to be enabled, so a
        // disabled module starts no initialization logic.
        if (enabled) {
            initializeConfig(config);
        }
        return enabled;
    }

    /**
     * The key this module's entries live under in the plugin's {@code modules.yml}.<br>
     * <br>
     * Spaces in the module name map to underscores. Both the {@code enabled} flag and the
     * {@code modulePrefix} string are read through this one method so they cannot disagree: they
     * used to derive the key separately, and for a module whose name contained a space they
     * derived two differently-spelled keys off the same name.
     */
    @Internal
    public final @NotNull String getModulesConfigKey() {
        return "modules." + getName().replace(" ", "_");
    }

    @Override
    public final @NotNull VersionedComponent getPrefix() {
        KamiConfigExt c = getPlugin().getModulesConfig();
        String key = getModulesConfigKey() + ".modulePrefix";
        String def = defaultPrefix().serializeMiniMessage();

        // Warn if the module does not have a prefix entry in the config so the plugin author can go add a default in the resource file
        // Through the plugin logger, NOT getLogger(). The subsystem logger is assigned in
        // AbstractSubsystem#handleEnable, so it is null anywhere this runs before enable. Nothing
        // reaches getPrefix() that early today, but ModuleConfig#isEnabledInConfig had exactly this
        // shape and did, and the resulting NPE surfaced as an unrelated registration failure.
        if (!c.contains(key)) {
            getPlugin().getLogger().warning(
                    "Module '" + getName() + "' missing string key '" + key + "' in the modules config. Using default: " + def
            );
        }

        return ColoredStringParser.parse(
                c.getString(key, def)
        );
    }

    /**
     * The data folder for this module, located at:<br>
     * /home/container/plugins/&lt;plugin&gt;/modules/&lt;module&gt;/<br>
     * <br>
     * If the folder does not exist, it will be created automatically.
     */
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

    /**
     * The absolute path to the data folder for this module, i.e.:<br>
     * /home/container/plugins/&lt;plugin&gt;/modules/&lt;module&gt;/<br>
     * <br>
     * This is just the File's absolute path, it does not create the folder if it does not exist.
     */
    @NotNull
    public String getModuleDataPath() {
        File dataFolder = getPlugin().getDataFolder();
        File moduleFolder = new File(dataFolder + File.separator + MODULES_FOLDER + File.separator + getName());
        return moduleFolder.getAbsolutePath();
    }
}
