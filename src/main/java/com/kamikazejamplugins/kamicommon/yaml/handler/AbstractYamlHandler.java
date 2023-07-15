package com.kamikazejamplugins.kamicommon.yaml.handler;

import com.kamikazejamplugins.kamicommon.yaml.MemoryConfiguration;
import com.kamikazejamplugins.kamicommon.yaml.YamlConfiguration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public abstract class AbstractYamlHandler {
    protected final File configFile;
    protected final String fileName;
    protected YamlConfiguration config;

    public AbstractYamlHandler(File configFile) {
        this.configFile = configFile;
        this.fileName = configFile.getName();
        this.config = null;
    }

    public AbstractYamlHandler(File configFile, String fileName) {
        this.configFile = configFile;
        this.fileName = fileName;
        this.config = null;
    }

    public YamlConfiguration loadConfig(boolean addDefaults) {
        LoaderOptions options = new LoaderOptions();
        options.setProcessComments(true);
        Yaml yaml = (new Yaml(options));

        try {
            if (!configFile.exists()) {
                if (!configFile.getParentFile().exists()) {
                    if (!configFile.getParentFile().mkdirs()) {
                        error("Could not create config file dirs for (" + configFile.getAbsolutePath() + "), stopping");
                    }
                }
                if (!configFile.createNewFile()) {
                    error("Could not create config file, stopping");
                    System.exit(0);
                }
            }

            Reader reader = Files.newBufferedReader(configFile.toPath(), StandardCharsets.UTF_8);
            config = new YamlConfiguration((MappingNode) yaml.compose(reader), configFile);
            if (addDefaults) { addDefaults(); }
            return config.save();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return createNewConfig();
    }

    private YamlConfiguration createNewConfig() {
        return new YamlConfiguration(createNewMappingNode(), configFile);
    }

    public static MappingNode createNewMappingNode() {
        return new MappingNode(Tag.MAP, new ArrayList<>(), DumperOptions.FlowStyle.AUTO);
    }

    private void save() {
        if (config != null) { config.save(); }
    }

    private void addDefaults() {
        InputStream defConfigStream = getIS();
        if (defConfigStream == null) {
            error("Error: Could NOT find config resource (" + configFile.getName() + "), could not add defaults!");
            save();
            return;
        }

        // InputStream and Reader both contain comments (verified)
        Reader reader = new InputStreamReader(defConfigStream, StandardCharsets.UTF_8);
        LoaderOptions options = new LoaderOptions();
        options.setProcessComments(true);

        MemoryConfiguration defConfig = new MemoryConfiguration((MappingNode) (new Yaml(options)).compose(reader));
        Set<String> keys = defConfig.getKeys(true);

        // Make a new config with the keys
        for (String key : keys) {
            // Don't overwrite existing keys
            if (!config.contains(key)) { config.set(key, defConfig.get(key)); }
        }
        config.copyCommentsFromDefault(keys, defConfig);
        save();
    }

    public abstract InputStream getIS();

    public abstract void error(String s);

    private int getHighest(Set<Integer> set) {
        int highest = 0;
        for (int i : set) { if (i > highest) { highest = i; } }
        return highest;
    }

    private int findLineOfKey(List<String> lines, String key) {
        String[] parts = key.split("\\.");
        int searchingFor = 0;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String part = parts[searchingFor];
            //Integer keys are wrapped in ''
            if (isInteger(part)) { part = "'" + part + "'"; }

            String start = repeat("  ", searchingFor) + part + ":";
            if (line.startsWith(start)) {
                if (searchingFor == parts.length - 1) {
                    // We've found the key we're looking for
                    return i+1;
                } else {
                    searchingFor++;
                }
            }
        }
        return -1;
    }

    @SuppressWarnings("SameParameterValue")
    private String repeat(String s, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    private boolean isInteger(String s) {
        try { Integer.parseInt(s); return true;
        } catch (NumberFormatException e) { return false; }
    }

    private boolean equalLists(List<String> l1, Set<String> l2) {
        // Check if l1 and l2 have the same items, regardless of order
        if (l1.size() != l2.size()) {
            return false;
        }
        for (String item : l1) {
            if (!l2.contains(item)) {
                return false;
            }
        }
        return true;
    }
}
