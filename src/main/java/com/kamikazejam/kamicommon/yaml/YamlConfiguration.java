package com.kamikazejam.kamicommon.yaml;

import com.kamikazejam.kamicommon.KamiCommon;
import org.yaml.snakeyaml.nodes.MappingNode;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@SuppressWarnings({"unused"})
public class YamlConfiguration extends MemorySection {
    private final File configFile;
    public YamlConfiguration(MappingNode node, File configFile) {
        super(node);
        this.configFile = configFile;
    }

    /**
     * Saves the config to the file
     * @return true IFF the config was saved successfully (can be skipped if the config is not changed)
     */
    public boolean save() {
        if (!isChanged()) { return false; }

        try {
            // Dump the Node (should keep comments)
            Writer writer = new OutputStreamWriter(Files.newOutputStream(configFile.toPath()), StandardCharsets.UTF_8);
            KamiCommon.getYaml().serialize(this.getNode(), writer);
            setChanged(false);
            return true;

        } catch(IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
