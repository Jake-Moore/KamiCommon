package com.kamikazejamplugins.kamicommon.yaml;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;

@SuppressWarnings({"unused"})
public class YamlConfiguration extends MemorySection {
    private final File configFile;
    public YamlConfiguration(LinkedHashMap<String, Object> data, File configFile) {
        super(data);
        this.configFile = configFile;
    }

    public YamlConfiguration save() {
        try {
            DumperOptions options = new DumperOptions();
            options.setIndent(2);
            options.setPrettyFlow(true);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setAllowUnicode(true);

            new Yaml(options).dump(this.getData(), new FileWriter(configFile));
        } catch(IOException e) {
            e.printStackTrace();
        }
        return this;
    }
}
