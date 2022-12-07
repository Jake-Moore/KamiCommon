package com.kamikazejamplugins.kamicommon.yaml;


import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        List<String> keys = getInputStreamKeys(getIS(plugin));
        LinkedHashMap<String, Object> data = (new Yaml()).load(defConfigStream);
        MemoryConfiguration defConfig = new MemoryConfiguration(data);

        if (!equalLists(keys, defConfig.getKeys(true))) {
            System.out.println(ANSI.RED
                    + "Warning: Error grabbing ordered defaults from (" + configFile.getName() + ")!"
                    + ANSI.RESET);
            save();
            return config;
        }

        for (String key : keys) {
            if (!config.contains(key)) {
                config.set(key, defConfig.get(key));
            }
        }
        save();
        return config;
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

    private List<String> getInputStreamKeys(InputStream defConfigStream) {
        List<String> keys = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(defConfigStream));
        try {
            // WhiteSpace, Keys
            Map<String, List<String>> map = new HashMap<>();

            Pattern pattern = Pattern.compile("^( *)([A-z0-9-_]+):");

            List<String> building = new ArrayList<>();
            int lastWhitespace = -1;
            while(reader.ready()) {
                String line = reader.readLine();
                Matcher m = pattern.matcher(line);
                if (m.find()) {
                    String whiteSpace = m.group(1);
                    String key = m.group(2);

                    if (whiteSpace.length() > lastWhitespace) {
                        building.add(key);
                        lastWhitespace = whiteSpace.length();
                    }else if (whiteSpace.length() == lastWhitespace) {
                        //Form a key from the previous additions
                        StringBuilder sb = new StringBuilder();
                        for (String s : building) {
                            sb.append(s).append(".");
                        }
                        keys.add(sb.substring(0, sb.length() - 1));

                        //Set the last one to this key so we may continue
                        building.set(building.size() - 1, key);
                    }else {
                        StringBuilder sb = new StringBuilder();
                        if (whiteSpace.length() == 0) {
                            //Form a key from the previous additions
                            for (String s : building) {
                                sb.append(s).append(".");
                            }
                            keys.add(sb.substring(0, sb.length() - 1));

                            building.clear();
                            building.add(key);
                            lastWhitespace = 0;
                        }else {
                            //Form a key from the previous additions
                            for (String s : building) {
                                sb.append(s).append(".");
                            }
                            keys.add(sb.substring(0, sb.length() - 1));

                            int diff = lastWhitespace - whiteSpace.length();
                            int times = diff / 2;

                            // Remove the amount of keys equals to number of tabs +1 (to make space for new key)
                            for (int i = 0; i <= times; i++) {
                                if (building.size() > 0) {
                                    building.remove(building.size() - 1);
                                }
                            }

                            //Set the last one to this key so we may continue
                            building.add(key);
                            lastWhitespace = whiteSpace.length();
                        }
                    }
                }
            }

            //Form a key from whatever is left
            StringBuilder sb = new StringBuilder();
            for (String s : building) {
                sb.append(s).append(".");
            }
            keys.add(sb.substring(0, sb.length() - 1));
        }catch (Exception e) { e.printStackTrace(); }
        return keys;
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
