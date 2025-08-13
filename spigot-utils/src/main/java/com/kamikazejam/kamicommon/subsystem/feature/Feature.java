package com.kamikazejam.kamicommon.subsystem.feature;

import com.kamikazejam.kamicommon.KamiPlugin;
import com.kamikazejam.kamicommon.configuration.spigot.KamiConfigExt;
import com.kamikazejam.kamicommon.subsystem.AbstractSubsystem;
import com.kamikazejam.kamicommon.subsystem.SubsystemConfig;
import com.kamikazejam.kamicommon.subsystem.modules.Module;
import com.kamikazejam.kamicommon.util.Preconditions;
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

    /**
     * @return The default logging prefix for this subsystem (saved under the KamiPlugin featuresConfig featurePrefix)
     */
    @Override
    public abstract @NotNull String defaultPrefix();

    // -------------------------------------------- //
    // FEATURE CONFIG
    // -------------------------------------------- //
    @Override
    public @NotNull String getConfigName() {
        return getName() + "Feature.yml";
    }

    @Override
    protected @NotNull FeatureConfig createConfig() {
        // If the feature yml path is null, fail
        @Nullable String featureYmlPath = getPlugin().getFeatureYmlPath();
        Preconditions.checkNotNull(
                featureYmlPath,
                "Feature YML Path is null! This feature config cannot be loaded without a path!"
        );
        // Load the config from the path
        if (!featureYmlPath.endsWith("/")) { featureYmlPath += "/"; }
        String fileName = featureYmlPath + getConfigName();
        Preconditions.checkNotNull(
                SubsystemConfig.getIS(this, fileName),
                "Feature YML resource is invalid! ('" + fileName + "') This feature config cannot be loaded!"
        );

        return new FeatureConfig(this, fileName);
    }

    @Override
    public final @NotNull String getPrefix() {
        KamiConfigExt c = getPlugin().getModulesConfig();
        String prefix = c.getString("features." + getName() + ".featurePrefix", null);
        if (prefix != null) { return prefix; }

        String def = defaultPrefix();
        c.setString("features." + getName() + ".featurePrefix", def);
        c.save();
        return def;
    }

    @NotNull
    public File getFeatureDataFolder() {
        File dataFolder = getPlugin().getDataFolder();
        File featureFolder = new File(dataFolder + File.separator + FeatureConfig.FEATURES_FOLDER + File.separator + getName());
        if (!featureFolder.exists()) {
            boolean created = featureFolder.mkdirs();
            if (!created) {
                throw new IllegalStateException("Failed to create feature data folder: " + featureFolder.getAbsolutePath());
            }
        }
        return featureFolder;
    }
}
