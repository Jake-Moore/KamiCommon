package com.kamikazejam.kamicommon.subsystem.feature;

import com.kamikazejam.kamicommon.KamiPlugin;
import lombok.Getter;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class FeatureManager {
    private final Map<Class<? extends Feature>, Feature> featureMap = new HashMap<>();
    @Getter private final List<Feature> featureList = new ArrayList<>();

    private final KamiPlugin plugin;
    public FeatureManager(KamiPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerFeature(Feature feature) {
        try {
            if (!featureList.contains(feature)) {
                featureList.add(feature);
            }
            featureMap.put(feature.getClass(), feature);

            // Initialize the Config
            FeatureConfig config = feature.createConfig();
            feature.initializeConfig(config);

            // Enable the feature
            feature.handleEnable();

        } catch (Throwable e) {
            plugin.getLogger().warning("Can not register the feature: " + feature.getName());
            e.printStackTrace();
        }
    }

    @Internal
    public void unregister() {
        for (Feature feature : featureList) {
            disable(feature);
        }
    }

    // Private disable access. Once a Feature is enabled, it should not be disabled until the plugin is shut down.
    private boolean disable(Feature feature) {
        // only disable enabled features
        if (!feature.isSuccessfullyEnabled() || !feature.isEnabled()) { return false; }

        try {
            feature.handleDisable();
            featureMap.remove(feature.getClass());
            featureList.remove(feature);
            return true;
        } catch (Throwable e) {
            plugin.getLogger().warning("Can not disable the feature: " + feature.getName());
            e.printStackTrace();
        }
        return false;
    }

    public boolean enable(Feature feature) {
        // only disable enabled features
        if (feature.isEnabled()) { return false; }

        try {
            registerFeature(feature);
            return true;
        } catch (Throwable e) {
            plugin.getLogger().warning("Can not enable the feature: " + feature.getName());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get the origin feature class by the class name.
     *
     * @param clazz Feature class
     * @param <T>   Feature
     * @return Origin feature class, if not exist null.
     */
    public <T extends Feature> T get(Class<T> clazz) {
        Feature feature = featureMap.get(clazz);
        if (feature == null) {
            for (Feature features : featureList) {
                if (clazz.isInstance(features)) {
                    return clazz.cast(features);
                }
            }
        }
        if (clazz.isInstance(feature)) {
            return clazz.cast(feature);
        }
        return null;
    }

    @Nullable
    public Feature getFeatureByName(String name) {
        for (Feature feature : featureList) {
            if (feature.getName().equalsIgnoreCase(name)) {
                return feature;
            }
        }
        return null;
    }

    public final void onItemsAdderLoaded() {
        for (Feature feature : featureList) {
            if (!feature.isEnabled()) { continue; }
            feature.onItemsAdderLoaded();
        }
    }

    public final void onMythicMobsLoaded() {
        for (Feature feature : featureList) {
            if (!feature.isEnabled()) { continue; }
            feature.onMythicMobsLoaded();
        }
    }

    public final void onCitizensLoaded() {
        for (Feature feature : featureList) {
            if (!feature.isEnabled()) { continue; }
            feature.onCitizensLoaded();
        }
    }
}
