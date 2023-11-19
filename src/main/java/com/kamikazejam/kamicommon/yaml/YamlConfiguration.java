package com.kamikazejam.kamicommon.yaml;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
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

    public YamlConfiguration save() {
        try {
            DumperOptions options = new DumperOptions();
            options.setIndent(2);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setAllowUnicode(true);
            options.setProcessComments(true);
            options.setPrettyFlow(false); // When Disabled, [] will be used for empty lists instead of [\n]  (Keep Disabled)
            options.setSplitLines(false); // When Enabled, string lines might be split into multiple lines   (Keep Disabled)

            // Dump the Node (should keep comments)
            Writer writer = new OutputStreamWriter(Files.newOutputStream(configFile.toPath()), StandardCharsets.UTF_8);
            new Yaml(options).serialize(this.getNode(), writer);

        } catch(IOException e) {
            e.printStackTrace();
        }
        return this;
    }
}
