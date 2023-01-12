package com.kamikazejamplugins.kamicommon.yaml.handler;

import com.kamikazejamplugins.kamicommon.util.data.ANSI;
import com.kamikazejamplugins.kamicommon.yaml.MemoryConfiguration;
import com.kamikazejamplugins.kamicommon.yaml.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

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

            InputStream configStream = Files.newInputStream(configFile.toPath());
            LinkedHashMap<String, Object> data = (new Yaml()).load(configStream);
            if (data == null) {
                data = new LinkedHashMap<>();
            }

            config = new YamlConfiguration(data, configFile);
            return (addDefaults) ? addDefaults(config).save() : config.save();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return new YamlConfiguration(new LinkedHashMap<>(), configFile);
    }

    private void save() {
        if (config != null) {
            config.save();
        }
    }

    private YamlConfiguration addDefaults(YamlConfiguration config) {
        InputStream defConfigStream = getIS();
        if (defConfigStream == null) {
            error("Error: Could NOT find config resource (" + configFile.getName() + "), could not add defaults!");
            save();
            return config;
        }

        MemoryConfiguration defConfig = new MemoryConfiguration((new Yaml()).load(defConfigStream));
        List<String> keys = getOrderedKeys(getIS(), defConfig.getKeys(true));

        if (!equalLists(keys, defConfig.getKeys(true))) {
            error("Error: Error grabbing ordered defaults from (" + configFile.getName() + ")!");
            save();
            return config;
        }

        // Add any existing keys that aren't in the defaults list
        // this will make any keys set by the plugin, that aren't in the defaults, stay
        List<String> existingKeys = new ArrayList<>(config.getKeys(true));
        for (String key : existingKeys) {
            if (!keys.contains(key)) { keys.add(key); }
        }

        // Make a new config so we can force the order of the keys when creating it
        YamlConfiguration newConfig = new YamlConfiguration(new LinkedHashMap<>(), configFile);
        for (String key : keys) {
            Object o = (config.contains(key)) ? config.get(key) : defConfig.get(key);
            newConfig.set(key, o);
        }
        save();
        return newConfig;
    }

    public abstract InputStream getIS();

    public abstract void error(String s);

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
                if (i < lines.size()) { i = lines.size() + 1; }else { i++; }
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
