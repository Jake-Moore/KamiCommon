package com.kamikazejam.kamicommon.subsystem.feature;

import com.kamikazejam.kamicommon.KamiPlugin;
import com.kamikazejam.kamicommon.configuration.spigot.KamiConfigExt;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.subsystem.AbstractSubsystem;
import com.kamikazejam.kamicommon.subsystem.SubsystemConfig;
import com.kamikazejam.kamicommon.subsystem.module.Module;
import com.kamikazejam.kamicommon.util.ColoredStringParser;
import org.jetbrains.annotations.NotNull;

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
        @NotNull String ymlPath = this.getPlugin().getFeatureYmlPath();
        if (ymlPath.endsWith("/")) {
            return ymlPath + this.getName() + "Feature.yml";
        } else {
            return ymlPath + "/" + this.getName() + "Feature.yml";
        }
    }

    @Override
    protected @NotNull FeatureConfig createConfig() {
        @NotNull String configResourcePath = this.getConfigResourcePath();
        // Double check we can obtain the resource stream (may throw)
        SubsystemConfig.getIS(this, configResourcePath);

        return new FeatureConfig(this, configResourcePath);
    }

    @Override
    public final @NotNull VersionedComponent getPrefix() {
        KamiConfigExt c = getPlugin().getModulesConfig();
        String prefix = c.getString("features." + getName() + ".featurePrefix", null);
        if (prefix != null) { return ColoredStringParser.parse(prefix); }

        VersionedComponent def = defaultPrefix();
        c.setString("features." + getName() + ".featurePrefix", def.serializeMiniMessage());
        c.save();
        return def;
    }

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
}
