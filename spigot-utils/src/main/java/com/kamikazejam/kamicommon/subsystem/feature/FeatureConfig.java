package com.kamikazejam.kamicommon.subsystem.feature;

import com.kamikazejam.kamicommon.subsystem.SubsystemConfig;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.yaml.source.ConfigSource;
import org.jetbrains.annotations.NotNull;

/**
 * The configuration of one {@link Feature}, defaulted from that feature's resource in the plugin jar and
 * written under the plugin's data folder. It is created lazily by {@link Feature#createConfig()}, so a
 * feature reaches its own with {@link Feature#getConfig()} rather than constructing this directly.
 *
 * @see <a href="https://github.com/Jake-Moore/KamiCommon/wiki/v5-Subsystems#config-layout">Subsystem config layout (wiki)</a>
 */
@SuppressWarnings("unused")
public class FeatureConfig extends SubsystemConfig<Feature> {

    // Constructor for features storing configs in Files on the server filesystem
    //   resourcePath is in form: "featureYmlPath + <feature>.yml"
    public FeatureConfig(@NotNull Feature feature, @NotNull String resourcePath) {
        super(
                Preconditions.checkNotNull(feature, "Feature cannot be null"),
                Preconditions.checkNotNull(resourcePath, "File name cannot be null")
        );
    }

    // Constructor for features using ConfigSource (e.g. from a database or other source)
    public FeatureConfig(@NotNull Feature feature, @NotNull ConfigSource source, @NotNull String resourcePath) {
        super(
                Preconditions.checkNotNull(feature, "Feature cannot be null"),
                Preconditions.checkNotNull(source, "ConfigSource cannot be null"),
                Preconditions.checkNotNull(resourcePath, "Resource path cannot be null")
        );
    }

    @NotNull
    public Feature getFeature() {
        return this.getSubsystem();
    }
}
