package com.kamikazejamplugins.kamicommon.yaml;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

            OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(configFile.toPath()), StandardCharsets.UTF_8);
            new Yaml(options).dump(this.getData(), writer);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return this;
    }
}
