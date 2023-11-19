package com.kamikazejam.kamicommon.yaml.handler;

import com.kamikazejam.kamicommon.configuration.config.AbstractConfig;
import com.kamikazejam.kamicommon.util.data.ANSI;
import com.kamikazejam.kamicommon.yaml.MemoryConfiguration;
import com.kamikazejam.kamicommon.yaml.YamlConfiguration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Tag;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public abstract class AbstractYamlHandler {
    protected final File configFile;
    protected final String fileName;
    protected YamlConfiguration config;
    protected final AbstractConfig abstractConfig;

    public AbstractYamlHandler(AbstractConfig abstractConfig, File configFile) {
        this.abstractConfig = abstractConfig;
        this.configFile = configFile;
        this.fileName = configFile.getName();
        this.config = null;
    }

    public AbstractYamlHandler(AbstractConfig abstractConfig, File configFile, String fileName) {
        this.abstractConfig = abstractConfig;
        this.configFile = configFile;
        this.fileName = fileName;
        this.config = null;
    }

    public YamlConfiguration loadConfig(boolean addDefaults) {
        return loadConfig(addDefaults, null);
    }

    public YamlConfiguration loadConfig(boolean addDefaults, @Nullable Supplier<InputStream> stream) {
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
            config = (addDefaults) ? addDefaults(stream) : config;
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

    private YamlConfiguration addDefaults(@Nullable Supplier<InputStream> defStreamSupplier) {
        // Use passed arg unless it's null, then grab the IS from the plugin
        InputStream defConfigStream = getIS(defStreamSupplier);

        // Error if we still don't have a default config stream
        if (defConfigStream == null) {
            error("Error: Could NOT find config resource (" + configFile.getName() + "), could not add defaults!");
            save();
            return createNewConfig();
        }

        // InputStream and Reader both contain comments (verified)
        Reader reader = new InputStreamReader(defConfigStream, StandardCharsets.UTF_8);
        LoaderOptions options = new LoaderOptions();
        options.setProcessComments(true);

        MemoryConfiguration defConfig = new MemoryConfiguration((MappingNode) (new Yaml(options)).compose(reader));
        // This should help preserve the order from the default config
        List<String> keys = getOrderedKeys(getIS(defStreamSupplier), defConfig.getKeys(true));

        // Add any existing keys that aren't in the defaults list
        // this will make any keys set by the plugin, that aren't in the defaults, stay
        List<String> existingKeys = new ArrayList<>(config.getKeys(true));
        for (String key : existingKeys) {
            if (!keys.contains(key)) { keys.add(key); }
        }

        YamlConfiguration newConfig = createNewConfig();

        // Make a new config with the keys
        for (String key : keys) {
            // Don't overwrite existing keys
            Object o = (config.contains(key)) ? config.get(key) : defConfig.get(key);
            newConfig.set(key, o);
        }
        // Copy comments the user might have placed in the file
        newConfig.copyCommentsFromDefault(keys, config, abstractConfig.isDefaultCommentsOverwrite());
        // Copy comments from the default config (they will override for each specific instance)
        newConfig.copyCommentsFromDefault(keys, defConfig, abstractConfig.isDefaultCommentsOverwrite());
        save();
        return newConfig;
    }

    private InputStream getIS(@Nullable Supplier<InputStream> defStreamSupplier) {
        return (defStreamSupplier == null) ? getIS() : defStreamSupplier.get();
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

    private List<String> getOrderedKeys(InputStream defConfigStream, Set<String> deepKeys) {
        List<String> keys = new ArrayList<>();
        Map<Integer, String> keyMappings = new HashMap<>();

        // Store the lines here so that we don't have to read the file multiple times
        List<String> lines = new BufferedReader(new InputStreamReader(defConfigStream, StandardCharsets.ISO_8859_1)).lines().collect(Collectors.toList());

        for (String key : deepKeys) {
            int lineNum = findLineOfKey(lines, key);
            if (lineNum < 0) {
                error("Could not find key: '" + key + "' in def config stream: " + configFile.getName() + ANSI.RESET);
                int i = getHighest(keyMappings.keySet());
                if (i < lines.size()) {
                    i = lines.size() + 1;
                } else {
                    i++;
                }
                keyMappings.put(i, key);
                continue;
            }
            keyMappings.put(lineNum, key);
        }

        List<Integer> keyLines = keyMappings.keySet().stream().sorted().collect(Collectors.toList());

        for (int keyLine : keyLines) {
            keys.add(keyMappings.get(keyLine));
        }
        return keys;
    }
}
