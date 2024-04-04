package com.kamikazejam.kamicommon.yaml.standalone;

import com.kamikazejam.kamicommon.yaml.AbstractYamlConfiguration;
import org.yaml.snakeyaml.nodes.MappingNode;

import java.io.File;

@SuppressWarnings({"unused"})
public class YamlConfigurationStandalone extends MemorySectionStandalone implements AbstractYamlConfiguration {
    private final File configFile;
    public YamlConfigurationStandalone(MappingNode node, File configFile) {
        super(node);
        this.configFile = configFile;
    }

    /**
     * Saves the config to the file
     * @return true IFF the config was saved successfully (can be skipped if the config is not changed)
     */
    @Override
    public boolean save() {
        return super.save(configFile);
    }
}
