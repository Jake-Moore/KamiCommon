package com.kamikazejamplugins.kamicommon.yaml;


import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.LinkedHashMap;

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
        InputStream defConfigStream;
        if (plugin != null) {
            defConfigStream = plugin.getResource(configFile.getName());
        }else {
            defConfigStream = getClass().getClassLoader().getResourceAsStream(File.separator + configFile.getName());
        }

        if (defConfigStream == null) {
            System.out.println(ANSI.RED
                    + "Warning: Could NOT find config resource (" + configFile.getName() + "), could not add defaults!"
                    + ANSI.RESET);
            save();
            return config;
        }
        MemoryConfiguration defConfig = new MemoryConfiguration((new Yaml()).load(defConfigStream));
        for (String key : defConfig.getKeys(true)) {
            if (!config.contains(key)) {
                config.set(key, defConfig.get(key));
            }
        }
        save();
        return config;
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
