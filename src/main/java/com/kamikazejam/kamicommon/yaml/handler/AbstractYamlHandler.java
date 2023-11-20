package com.kamikazejam.kamicommon.yaml.handler;

import com.kamikazejam.kamicommon.KamiCommon;
import com.kamikazejam.kamicommon.configuration.config.AbstractConfig;
import com.kamikazejam.kamicommon.util.data.Pair;
import com.kamikazejam.kamicommon.yaml.MemoryConfiguration;
import com.kamikazejam.kamicommon.yaml.YamlConfiguration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Tag;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "UnusedReturnValue"})
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
        long ms = System.currentTimeMillis();

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
            System.out.println("  Create took: " + (System.currentTimeMillis() - ms) + " ms."); ms = System.currentTimeMillis();

            Reader reader = Files.newBufferedReader(configFile.toPath(), StandardCharsets.UTF_8);
            System.out.println("  Reader took: " + (System.currentTimeMillis() - ms) + " ms."); ms = System.currentTimeMillis();

            config = new YamlConfiguration((MappingNode) KamiCommon.getYaml().compose(reader), configFile);
            System.out.println("  Compose took: " + (System.currentTimeMillis() - ms) + " ms."); ms = System.currentTimeMillis();

            if (addDefaults) {
                config = addDefaults(stream);
                System.out.println("  Add Defaults took: " + (System.currentTimeMillis() - ms) + " ms."); ms = System.currentTimeMillis();
            }

            boolean b = config.save();
            System.out.println("  Save (" + b + ") took: " + (System.currentTimeMillis() - ms) + " ms.");

            return config;
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


    /**
     * Saves the config to the file
     * @return true IFF the config was saved successfully (can be skipped if the config is not changed)
     */
    private boolean save() {
        if (config != null) { return config.save(); }
        return false;
    }

    private YamlConfiguration addDefaults(@Nullable Supplier<InputStream> defStreamSupplier) {
        long ms = System.currentTimeMillis();
        System.out.println("  START addDefaults");

        // Use passed arg unless it's null, then grab the IS from the plugin
        InputStream defConfigStream = getIS(defStreamSupplier);

        // Error if we still don't have a default config stream
        if (defConfigStream == null) {
            error("Error: Could NOT find config resource (" + configFile.getName() + "), could not add defaults!");
            save();
            return config;
        }
        System.out.println("    Get IS took: " + (System.currentTimeMillis() - ms) + " ms."); ms = System.currentTimeMillis();

        // InputStream and Reader both contain comments (verified)
        Reader reader = new InputStreamReader(defConfigStream, StandardCharsets.UTF_8);

        MemoryConfiguration defConfig = new MemoryConfiguration((MappingNode) (KamiCommon.getYaml()).compose(reader));
        System.out.println("    Compose took: " + (System.currentTimeMillis() - ms) + " ms."); ms = System.currentTimeMillis();

        boolean needsNewKeys = false;
        for (String key : defConfig.getKeys(true)) {
            if (!config.contains(key)) { needsNewKeys = true; break; }
        }
        System.out.println("    Needs New Keys took: " + (System.currentTimeMillis() - ms) + " ms."); ms = System.currentTimeMillis();
        System.out.println("      NeedsNewKeys: " + needsNewKeys);

        // This is a massive optimization, because if we need to insert new defaults, it requires a
        //  full recreate and rewrite of the config file, which is very slow
        if (!needsNewKeys) { return config; }

        Pair<List<String>, List<String>> pair = YAMLParser.parseOrderedKeys(getIS(defStreamSupplier));
        List<String> keys = pair.getA();
        List<String> allKeys = pair.getB();
        System.out.println("    Parse Keys took: " + (System.currentTimeMillis() - ms) + " ms."); ms = System.currentTimeMillis();

        // Add any existing keys that aren't in the defaults list
        // this will make any keys set by the plugin, that aren't in the defaults, stay
        for (String key : config.getKeys(true)) {
            if (!keys.contains(key)) { keys.add(key); }
        }
        System.out.println("    Add Existing Keys took: " + (System.currentTimeMillis() - ms) + " ms."); ms = System.currentTimeMillis();
        System.out.println("      Total Keys: " + keys.size());

        YamlConfiguration newConfig = createNewConfig();
        System.out.println("    Create New Config took: " + (System.currentTimeMillis() - ms) + " ms."); ms = System.currentTimeMillis();

        // Make a new config with the keys
        for (String key : keys) {
            Object o = config.get(key, null);
            if (o == null) { o = defConfig.get(key); }
            newConfig.set(key, o);
        }
        System.out.println("    Set Keys took: " + (System.currentTimeMillis() - ms) + " ms."); ms = System.currentTimeMillis();

        // Copy comments the user might have placed in the file
        newConfig.copyCommentsFromDefault(keys, config, abstractConfig.isDefaultCommentsOverwrite());
        // Copy comments from the default config (they will override for each specific instance)
        newConfig.copyCommentsFromDefault(allKeys, defConfig, abstractConfig.isDefaultCommentsOverwrite());
        System.out.println("    Copy Comments took: " + (System.currentTimeMillis() - ms) + " ms.");

        return newConfig;
    }

    private InputStream getIS(@Nullable Supplier<InputStream> defStreamSupplier) {
        return (defStreamSupplier == null) ? getIS() : defStreamSupplier.get();
    }

    public abstract InputStream getIS();

    public abstract void error(String s);

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

    public static class YAMLParser {
        public static Pair<List<String>, List<String>> parseOrderedKeys(InputStream stream) {
            List<String> valueKeys = new ArrayList<>();
            List<String> allKeys = new ArrayList<>();

            Iterable<Object> yamlObjects = KamiCommon.getYaml().loadAll(stream);
            for (Object yamlObject : yamlObjects) {
                processYAMLObject(yamlObject, valueKeys, allKeys, "");
            }

            return Pair.of(valueKeys, allKeys);
        }

        @SuppressWarnings("unchecked")
        private static void processYAMLObject(Object yamlObject, List<String> keysWithValues, List<String> allKeys, String parentKey) {
            if (yamlObject instanceof Map) {
                Map<String, Object> yamlMap = (Map<String, Object>) yamlObject;

                for (Map.Entry<String, Object> entry : yamlMap.entrySet()) {
                    String currentKey = parentKey.isEmpty() ? entry.getKey() : parentKey + "." + entry.getKey();
                    allKeys.add(currentKey);

                    // Add only keys that terminate in a value
                    Object value = entry.getValue();
                    if (value != null && !(value instanceof Map)) {
                        keysWithValues.add(currentKey);
                    }

                    if (value instanceof Map) {
                        processYAMLObject(value, keysWithValues, allKeys, currentKey);
                    }
                }
            }
        }
    }
}
