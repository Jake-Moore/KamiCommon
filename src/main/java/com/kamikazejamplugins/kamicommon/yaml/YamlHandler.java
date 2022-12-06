package com.kamikazejamplugins.kamicommon.yaml;


import com.kamikazejamplugins.kamicommon.util.StringUtil;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

@SuppressWarnings("unused")
public class YamlHandler {
    private final File configFile;
    private final String fileName;
    private final YamlConfiguration config;

    public YamlHandler(File configFile) {
        this.configFile = configFile;
        this.fileName = configFile.getName();
        this.config = null;
    }

    public YamlHandler(File configFile, String fileName) {
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

            if (addDefaults) {
                return (new YamlConfiguration(addDefaults(data), configFile)).save();
            }else {
                return (new YamlConfiguration(data, configFile)).save();
            }
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

    private LinkedHashMap<String, Object> addDefaults(LinkedHashMap<String, Object> config) {
        InputStream defConfigStream = getClass().getClassLoader().getResourceAsStream(File.separator + configFile.getName());
        if (defConfigStream == null) {
            System.out.println(ANSI.RED
                    + "Warning: Could NOT find config resource (" + configFile.getName() + "), could not add defaults!"
                    + ANSI.RESET);
            save();
            return config;
        }
        Map<String, Object> defConfig = (new Yaml()).load(defConfigStream);

        if (!defConfig.isEmpty()) {
            for (String key : defConfig.keySet()) {
                if (!config.containsKey(key)) {
                    config.put(key, defConfig.get(key));
                }
            }
        }
        save();
        return config;
    }

    @SuppressWarnings({"unused"})
    public static class YamlConfiguration extends MemorySection {
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

    public static class ConfigurationSection extends MemorySection {
        public ConfigurationSection(LinkedHashMap<String, Object> data) {
            super(data);
        }
    }

    @SuppressWarnings({"unchecked", "unused"})
    public static abstract class MemorySection {
        @Getter private final LinkedHashMap<String, Object> data;
        public MemorySection(LinkedHashMap<String, Object> data) {
            this.data = data;
        }

        public void set(String key, Object value) {
            put(key, value);
        }

        public void setItemStack(String key, ItemStack item) {
            set(key + ".type", item.getType().name());
            set(key + ".amount", item.getAmount());
            set(key + ".durability", item.getDurability());

            // Save basic string meta
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) { set(key + ".display-name", StringUtil.reverseT(meta.getDisplayName())); }
            if (meta.hasLore()) { set(key + ".lore", StringUtil.reverseT(meta.getLore())); }

            // Save ItemFlags
            List<String> flagNames = new ArrayList<>();
            for (ItemFlag flag : meta.getItemFlags()) { flagNames.add(flag.name()); }
            if (!flagNames.isEmpty()) { set(key + ".flags", flagNames); }

            // Save Enchants
            for (Map.Entry<Enchantment, Integer> e : meta.getEnchants().entrySet()) {
                set(key + ".enchantments." + e.getKey().getName(), e.getValue());
            }

            //Save unbreakable
            if (meta.spigot().isUnbreakable()) {
                set(key + ".unbreakable", true);
            }
        }

        public void put(String key, Object value) {
            //ItemStacks are special
            if (value instanceof ItemStack) { setItemStack(key, (ItemStack) value); return; }

            String[] keys = key.split("\\.");
            if (keys.length <= 1) { data.put(key, value); return; }

            LinkedHashMap<String, Object> dataRecent = data;
            for (int i = 0; i < keys.length-1; i++) {
                String k = keys[i];
                if (dataRecent.containsKey(k)) {
                    if (dataRecent.get(k) instanceof LinkedHashMap) {
                        dataRecent = (LinkedHashMap<String, Object>) dataRecent.get(k);
                    }else {
                        StringBuilder builder = new StringBuilder();
                        for (int j = 0; j <= i; j++) {
                            builder.append(keys[j]).append(" ");
                        }
                        String newKey = builder.toString().trim().replaceAll(" ", ".");
                        if (key.equalsIgnoreCase(newKey)) {
                            System.out.println(ANSI.RED + "Equal Keys: " + key);
                        }
                        put(newKey, new LinkedHashMap<>());

                        dataRecent = new LinkedHashMap<>();
                    }
                }else if (i != keys.length - 1){
                    StringBuilder builder = new StringBuilder();
                    for (int j = 0; j <= i; j++) {
                        builder.append(keys[j]).append(" ");
                    }
                    String newKey = builder.toString().trim().replaceAll(" ", ".");
                    put(newKey, new LinkedHashMap<>());

                    dataRecent = new LinkedHashMap<>();
                }
            }
            dataRecent.put(keys[keys.length - 1], value);

            LinkedHashMap<String, Object> newData = dataRecent;
            newData.put(keys[keys.length - 1], value);
            for (int j = keys.length - 2; j >= 1; j--){
                LinkedHashMap<String, Object> temp = data;
                for (int k = 0; k < j; k++) {
                    temp = (LinkedHashMap<String, Object>) temp.get(keys[k]);
                }
                temp.put(keys[j], newData);
                newData = temp;
            }

            data.put(keys[0], newData);
        }

        public Object get(String key) {
            String[] keys = key.split("\\.");
            LinkedHashMap<String, Object> map = data;
            for (int i = 0; i < keys.length-1; i++) {
                if (map.containsKey(keys[i])) {
                    map = (LinkedHashMap<String, Object>) map.get(keys[i]);
                } else {
                    return null;
                }
            }
            return map.get(keys[keys.length-1]);
        }

        public Object get(String key, Object def) {
            if (contains(key)) { return get(key);
            }else { return def; }
        }

        public void putString(String key, String value) {
            put(key, value);
        }

        public void putBoolean(String key, boolean value) {
            put(key, value);
        }

        public void putInteger(String key, int value) {
            put(key, value);
        }

        public void putLong(String key, long value) {
            put(key, value);
        }

        public void putDouble(String key, double value) {
            put(key, value);
        }

        public ConfigurationSection getConfigurationSection(String key) {
            return new ConfigurationSection((LinkedHashMap<String, Object>) get(key));
        }

        public String getString(String key) {
            return (String) get(key);
        }

        public String getString(String key, String def) {
            if (contains(key)) { return getString(key);
            }else { return def; }
        }

        public int getInt(String key) { return getInteger(key); }

        public int getInt(String key, int def) {
            if (contains(key)) { return getInt(key);
            }else { return def; }
        }

        public int getInteger(String key) {
            return Integer.parseInt(get(key).toString());
        }
        public int getInteger(String key, int def) { return getInt(key, def); }

        public long getLong(String key) {
            return Long.parseLong(get(key).toString());
        }
        public long getLong(String key, long def) {
            if (contains(key)) { return getLong(key);
            }else { return def; }
        }

        public boolean getBoolean(String key) {
            if (contains(key)) {
                return Boolean.parseBoolean(get(key).toString());
            }
            return false;
        }
        public boolean getBoolean(String key, boolean def) {
            if (contains(key)) { return getBoolean(key);
            }else { return def; }
        }

        public List<String> getStringList(String key) {
            if (contains(key)) {
                return (List<String>) get(key);
            }else {
                return new ArrayList<>();
            }
        }
        public List<String> getStringList(String key, List<String> def) {
            if (contains(key)) { return getStringList(key);
            }else { return def; }
        }

        public List<Integer> getIntegerList(String key) {
            if (contains(key)) {
                return (List<Integer>) get(key);
            }else {
                return new ArrayList<>();
            }
        }
        public List<Integer> getIntegerList(String key, List<Integer> def) {
            if (contains(key)) { return getIntegerList(key);
            }else { return def; }
        }

        public double getDouble(String key) {
            return (Double) get(key);
        }
        public double getDouble(String key, double def) {
            if (contains(key)) { return getDouble(key);
            }else { return def; }
        }

        public ItemStack getItemStack(String key) {
            if (!contains(key + ".type")) { return null; }
            if (!contains(key + ".amount")) { return null; }
            if (!contains(key + ".durability")) { return null; }
            Material type = Material.getMaterial(getString(key + ".type"));
            int amount = getInt(key + ".amount");
            short durability = (short) getInt(key + ".durability");

            ItemStack item = new ItemStack(type, amount, durability);
            ItemMeta meta = item.getItemMeta();
            if (contains(key + ".display-name")) { meta.setDisplayName(StringUtil.t(getString(key + ".display-name"))); }
            if (contains(key + ".lore")) { meta.setLore(StringUtil.t(getStringList(key + ".lore"))); }

            // Load ItemFlags
            if (contains(key + ".flags")) {
                for (String flagName : getStringList(key + ".flags")) {
                    meta.addItemFlags(ItemFlag.valueOf(flagName));
                }
            }

            // Load Enchants
            if (contains(key + ".enchantments")) {
                for (String enchantName : getConfigurationSection(key + ".enchantments").getKeys(false)) {
                    meta.addEnchant(Enchantment.getByName(enchantName), getInt(key + ".enchantments." + enchantName), true);
                }
            }

            // Load unbreakable
            if (contains(key + ".unbreakable")) {
                meta.spigot().setUnbreakable(getBoolean(key + ".unbreakable"));
            }

            item.setItemMeta(meta);
            return item;
        }

        public ItemStack getItemStack(String key, ItemStack def) {
            if (contains(key)) { return getItemStack(key);
            }else { return def; }
        }

        /**
         * Returns the keys of the config
         * If Deep is enabled, it will dig and find all valid keys that resolve to a value
         * @param deep Whether to search for all sub-keys
         * @return The list of keys found
         */
        public Set<String> getKeys(boolean deep) {
            if (!deep) {
                return data.keySet();
            }else {
                Set<String> keys = new HashSet<>();

                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    if (entry.getValue() instanceof LinkedHashMap) {
                        for (String k : getConfigurationSection(entry.getKey()).getKeys(true)) {
                            keys.add(entry.getKey() + "." + k);
                        }
                    }else {
                        keys.add(entry.getKey());
                    }
                }
                return keys;
            }
        }

        public boolean isConfigurationSection(final String path) {
            return get(path) instanceof LinkedHashMap;
        }

        public boolean contains(String key) {
            String[] keys = key.split("\\.");

            LinkedHashMap<String, Object> map = data;
            for (int i = 0; i < keys.length-1; i++) {
                if (map.containsKey(keys[i]) && map.get(keys[i]) instanceof LinkedHashMap) {
                    map = (LinkedHashMap<String, Object>) map.get(keys[i]);
                } else {
                    return false;
                }
            }
            return map.containsKey(keys[keys.length - 1]);
        }
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
