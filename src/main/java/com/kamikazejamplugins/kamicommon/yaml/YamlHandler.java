package com.kamikazejamplugins.kamicommon.yaml;


import com.kamikazejamplugins.kamicommon.util.StringUtil;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class YamlHandler {
    @Nullable private final JavaPlugin plugin;
    private final File configFile;
    private final String fileName;
    private final YamlConfiguration config;

    public YamlHandler(@Nullable JavaPlugin plugin, File configFile) {
        this.plugin = plugin;
        this.configFile = configFile;
        this.fileName = configFile.getName();
        this.config = null;
    }

    public YamlHandler(@Nullable JavaPlugin plugin, File configFile, String fileName) {
        this.plugin = plugin;
        this.configFile = configFile;
        this.fileName = fileName;
        this.config = null;
    }

    public YamlConfiguration loadConfig(boolean addDefaults) {
        try {
            if (!configFile.exists()) {
                if (!configFile.getParentFile().exists()) {
                    if (!configFile.getParentFile().mkdirs()) {
                        System.out.println("Could not create config file dirs, stopping");
                    }
                }
                if (!configFile.createNewFile()) {
                    System.out.println("Could not create config file, stopping");
                    System.exit(0);
                }
            }

            InputStream configStream = Files.newInputStream(configFile.toPath());
            LinkedHashMap<String, Object> data = (new Yaml()).load(configStream);
            if (data == null) {
                data = new LinkedHashMap<>();
            }

            YamlConfiguration configuration = new YamlConfiguration(data, configFile);
            return (addDefaults) ? addDefaults(configuration).save() : configuration.save();
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
        InputStream defConfigStream = getIS(plugin);

        if (defConfigStream == null) {
            System.out.println(ANSI.RED
                    + "Warning: Could NOT find config resource (" + configFile.getName() + "), could not add defaults!"
                    + ANSI.RESET);
            save();
            return config;
        }

        MemoryConfiguration defConfig = new MemoryConfiguration((new Yaml()).load(defConfigStream));
        List<String> keys = getOrderedKeys(getIS(plugin), defConfig.getKeys(true));

        if (!equalLists(keys, defConfig.getKeys(true))) {
            System.out.println(ANSI.RED
                    + "Warning: Error grabbing ordered defaults from (" + configFile.getName() + ")!"
                    + ANSI.RESET);
            save();
            return config;
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

    private List<String> getOrderedKeys(InputStream defConfigStream, Set<String> deepKeys) {
        List<String> keys = new ArrayList<>();
        Map<Integer, String> keyMappings = new HashMap<>();

        // Store the lines here so that we don't have to read the file multiple times
        List<String> lines = new BufferedReader(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)).lines().collect(Collectors.toList());

        for (String key : deepKeys) {
            int lineNum = findLineOfKey(lines, key);
            if (lineNum < 0) {
                System.out.println(ANSI.RED + "Could not find key: '" + key + "' in config file: " + configFile.getName() + ANSI.RESET);
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

    private int findLineOfKey(List<String> lines, String key) {
        String[] parts = key.split("\\.");
        int searchingFor = 0;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String start = StringUtil.repeat("  ", searchingFor) + parts[searchingFor] + ":";

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



    private InputStream getIS(@Nullable JavaPlugin plugin) {
        if (plugin != null) {
            return plugin.getResource(configFile.getName());
        }else {
            return getClass().getClassLoader().getResourceAsStream(File.separator + configFile.getName());
        }
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

    public static class ANSI {
        public static final String RESET = "\u001B[0m";
        public static final String BLACK = "\u001B[30m";
        public static final String RED = "\u001B[31m";
        public static final String GREEN = "\u001B[32m";
        public static final String YELLOW = "\u001B[33m";
        public static final String BLUE = "\u001B[34m";
        public static final String PURPLE = "\u001B[35m";
        public static final String CYAN = "\u001B[36m";
        public static final String WHITE = "\u001B[37m";
    }
}
