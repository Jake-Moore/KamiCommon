package com.kamikazejam.kamicommon.subsystem.feature;

import com.kamikazejam.kamicommon.subsystem.SubsystemConfig;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.yaml.source.ConfigSource;
import org.jetbrains.annotations.NotNull;

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

    private String getFeatureConfigKey() {
        return "features." + this.getFeature().getName().replace(" ", "_");
    }
}
