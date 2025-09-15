package com.kamikazejam.kamicommon.subsystem.feature;

import com.kamikazejam.kamicommon.configuration.spigot.KamiConfigExt;
import com.kamikazejam.kamicommon.subsystem.SubsystemConfig;
import com.kamikazejam.kamicommon.util.Preconditions;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class FeatureConfig extends SubsystemConfig<Feature> {

    // resourcePath is in form: "featureYmlPath + <feature>.yml"
    public FeatureConfig(@NotNull Feature feature, @NotNull String resourcePath) {
        super(
                Preconditions.checkNotNull(feature, "Feature cannot be null"),
                Preconditions.checkNotNull(resourcePath, "File name cannot be null")
        );
    }

    @NotNull
    public Feature getFeature() {
        return this.getSubsystem();
    }

    @Override
    public final void addConfigDefaults() {
        Feature feature = this.getFeature();
        KamiConfigExt c = feature.getPlugin().getFeaturesConfig();
        c.addDefault(getFeatureConfigKey() + ".featurePrefix", feature.defaultPrefix().serializeMiniMessage());
        c.save();

        this.save();
        this.reload();
    }

    private String getFeatureConfigKey() {
        return "features." + this.getFeature().getName().replace(" ", "_");
    }
}
