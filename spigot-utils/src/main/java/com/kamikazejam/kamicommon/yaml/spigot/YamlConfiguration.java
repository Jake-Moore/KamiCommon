package com.kamikazejam.kamicommon.yaml.spigot;

import com.kamikazejam.kamicommon.snakeyaml.nodes.MappingNode;
import com.kamikazejam.kamicommon.yaml.AbstractYamlConfiguration;

import java.io.File;

@SuppressWarnings({"unused"})
public class YamlConfiguration extends MemorySection implements AbstractYamlConfiguration {
    private final File configFile;
    public YamlConfiguration(MappingNode node, File configFile) {
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
