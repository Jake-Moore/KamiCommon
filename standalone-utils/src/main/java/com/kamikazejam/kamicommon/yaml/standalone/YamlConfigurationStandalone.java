package com.kamikazejam.kamicommon.yaml.standalone;

import com.kamikazejam.kamicommon.yaml.AbstractYamlConfiguration;
import com.kamikazejam.kamicommon.yaml.source.ConfigSource;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.nodes.MappingNode;

@SuppressWarnings({"unused"})
public class YamlConfigurationStandalone extends MemorySectionStandalone implements AbstractYamlConfiguration {
    private final @NotNull ConfigSource source;

    public YamlConfigurationStandalone(@NotNull MappingNode node, @NotNull ConfigSource source) {
        super(node, "", null);
        this.source = source;
    }

    /**
     * Saves the config to the backing source
     * @return true IFF the config was saved successfully (can be skipped if the config is not changed)
     */
    @Override
    public boolean save() {
        return super.save(source);
    }

    /**
     * Saves the config to the backing source
     * @param force If the config should be saved even if no changes were made
     * @return IFF the config was saved (can be skipped if no changes were made and force is false)
     */
    @Override
    public boolean save(boolean force) {
        return super.save(source, force);
    }
}
