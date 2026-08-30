package com.kamikazejam.kamicommon.subsystem.feature;

import com.kamikazejam.kamicommon.KamiPlugin;
import com.kamikazejam.kamicommon.configuration.spigot.KamiConfigExt;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.subsystem.AbstractSubsystem;
import com.kamikazejam.kamicommon.subsystem.SubsystemConfig;
import com.kamikazejam.kamicommon.subsystem.module.Module;
import com.kamikazejam.kamicommon.util.ColoredStringParser;
import com.kamikazejam.kamicommon.util.Preconditions;
import org.jetbrains.annotations.ApiStatus.OverrideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * This class represents a single feature registered under your {@link KamiPlugin} plugin.<br>
 * A feature is a subsystem that acts like its own plugin, providing its own functionality and configuration.<br>
 * Features cannot be disabled or toggled, they are always enabled.<br>
 * For a subsystem that can be toggled or disabled, see {@link Module}.<br>
 */
@SuppressWarnings("unused")
public abstract class Feature extends AbstractSubsystem<FeatureConfig, Feature> {
    public static final @NotNull String FEATURES_FOLDER = "features";

    /**
     * @return The default logging prefix for this subsystem (saved under the KamiPlugin featuresConfig featurePrefix)
     */
    @Override
    public abstract @NotNull VersionedComponent defaultPrefix();

    // -------------------------------------------- //
    // FEATURE CONFIG
    // -------------------------------------------- //
    @Override
    public @NotNull File getConfigFileDestination() {
        // Default: /home/container/plugins/<plugin>/features/<feature>.yml
        return new File(this.getPlugin().getDataFolder() + File.separator + FEATURES_FOLDER + File.separator + this.getName() + ".yml");
    }

    /**
     * The name of the config file (IN SOURCE CODE) for this Feature.<br>
     * <br>
     * By default, it is fetched as {@link KamiPlugin#getFeatureYmlPath()}/[name]Feature.yml<br>
     * <br>
     * You can override this method, or edit {@link KamiPlugin#getFeatureYmlPath()} to change the path resolution.
     */
    public @NotNull String getConfigResourcePath() {
        @Nullable String rawYmlPath = this.getPlugin().getFeatureYmlPath();
        // KamiPlugin#getFeatureYmlPath() returns null by default, and this method is @NotNull, so
        // dereferencing it produced a bare NullPointerException that FeatureManager#registerFeature
        // then reported only as "Can not register the feature". v4 failed here with a Preconditions
        // message; this restores one that names the method the plugin has to override.
        @NotNull String ymlPath = Preconditions.checkNotNull(
                rawYmlPath,
                "Feature '" + this.getName() + "' cannot resolve its config resource: "
                        + this.getPlugin().getName() + " does not override KamiPlugin#getFeatureYmlPath(), "
                        + "which returns null by default. Override it to return the jar subpackage "
                        + "holding your feature yml resources, or override Feature#getConfigResourcePath()."
        );
        if (ymlPath.endsWith("/")) {
            return ymlPath + this.getName() + "Feature.yml";
        } else {
            return ymlPath + "/" + this.getName() + "Feature.yml";
        }
    }

    @OverrideOnly
    @Override
    public @NotNull FeatureConfig createConfig() {
        @NotNull String configResourcePath = this.getConfigResourcePath();
        // Double check we can obtain the resource stream (may throw)
        SubsystemConfig.getIS(this, configResourcePath);

        return new FeatureConfig(this, configResourcePath);
    }

    @Override
    protected @NotNull String getSubsystemConfigSection() {
        // The 'features' section of the plugin's features.yml. Not FEATURES_FOLDER: that names the
        // directory on disk, and the two only look alike.
        return "features";
    }

    @Override
    public final @NotNull VersionedComponent getPrefix() {
        KamiConfigExt c = getPlugin().getFeaturesConfig();
        // Was "features." + getName() + ".featurePrefix", the raw name, while modules normalised
        // spaces. Both now go through AbstractSubsystem#getSubsystemConfigKey.
        String key = getSubsystemConfigKey() + ".featurePrefix";
        String def = defaultPrefix().serializeMiniMessage();

        // Warn if the feature does not have a prefix entry in the config so the plugin author can go add a default in the resource file
        // Through the plugin logger, NOT getLogger(): the subsystem logger is assigned in
        // AbstractSubsystem#handleEnable and is null anywhere this runs before enable. Same shape,
        // and same reasoning, as Module#getPrefix().
        if (!c.contains(key)) {
            getPlugin().getLogger().warning(
                    "Feature '" + getName() + "' missing string key '" + key + "' in the features config. Using default: " + def
            );
        }

        return ColoredStringParser.parse(
                c.getString(key, def)
        );
    }

    /**
     * The data folder for this feature, located at:<br>
     * /home/container/plugins/&lt;plugin&gt;/features/&lt;feature&gt;/<br>
     * <br>
     * If the folder does not exist, it will be created automatically.
     */
    @NotNull
    public File getFeatureDataFolder() {
        File dataFolder = getPlugin().getDataFolder();
        File featureFolder = new File(dataFolder + File.separator + FEATURES_FOLDER + File.separator + getName());
        if (!featureFolder.exists()) {
            boolean created = featureFolder.mkdirs();
            if (!created) {
                throw new IllegalStateException("Failed to create feature data folder: " + featureFolder.getAbsolutePath());
            }
        }
        return featureFolder;
    }

    /**
     * The absolute path to the data folder for this feature, i.e.:<br>
     * /home/container/plugins/&lt;plugin&gt;/features/&lt;feature&gt;/<br>
     * <br>
     * This is just the File's absolute path, it does not create the folder if it does not exist.
     */
    @NotNull
    public String getFeatureDataPath() {
        File dataFolder = getPlugin().getDataFolder();
        File featureFolder = new File(dataFolder + File.separator + FEATURES_FOLDER + File.separator + getName());
        return featureFolder.getAbsolutePath();
    }
}
