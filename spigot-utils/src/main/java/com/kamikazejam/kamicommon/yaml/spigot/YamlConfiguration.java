package com.kamikazejam.kamicommon.yaml.spigot;

import com.kamikazejam.kamicommon.yaml.AbstractYamlConfiguration;
import org.yaml.snakeyaml.nodes.MappingNode;

import java.io.File;

@SuppressWarnings({"unused"})
public class YamlConfiguration extends MemorySection implements AbstractYamlConfiguration {
    private final File configFile;
    public YamlConfiguration(MappingNode node, File configFile) {
        super(node, "", null);
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

    /**
     * Saves the config to the file
     * @param force If the config should be saved even if no changes were made
     * @return IFF the config was saved (can be skipped if no changes were made and force is false)
     */
    @Override
    public boolean save(boolean force) {
        return super.save(configFile, force);
    }
}
