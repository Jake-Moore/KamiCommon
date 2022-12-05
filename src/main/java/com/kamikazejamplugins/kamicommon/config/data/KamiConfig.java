package com.kamikazejamplugins.kamicommon.config.data;

import com.kamikazejamplugins.kamicommon.config.KamiConfigManager;
import com.kamikazejamplugins.kamicommon.yaml.YamlHandler;
import lombok.Getter;

import java.io.File;
import java.util.*;

/**
 * A class that represents a configuration file <p>
 * Key methods are {@link KamiConfig#addCommentAbove(String, String...)} and {@link KamiConfig#addCommentInline(String, String)} <p>
 * This is an extension of a YamlConfiguration, so all get, set, and put methods are available. <p>
 * <p></p>
 * When extending this class, provide the File to the config in the super, and then add all desired comments <p>
 * Then you can use this object just like a YamlConfiguration, it has all the same methods plus {@link KamiConfig#save()} and {@link KamiConfig#reload()} <p>
 */

@SuppressWarnings("unused")
public abstract class KamiConfig {
    @Getter private final File file;
    private final YamlHandler yamlHandler;
    private YamlHandler.YamlConfiguration config;

    // Key, Comment
    @Getter private final List<ConfigComment> comments = new ArrayList<>();
    private Thread thread = null;

    public KamiConfig(File file) {
        this.file = file;

        // Ensure the file exists
        try {
            if (!file.exists() && !file.createNewFile()) {
                throw new Exception("Failed to create file");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        this.yamlHandler = new YamlHandler(file);
        this.config = yamlHandler.loadConfig(true);
        save();
    }

    /**
     * Adds a comment above the specified key
     * @param key The key to add the comment above
     * @param comment The comment to add (can be multiple lines separated by \n)
     * Note: Each string will split lines by "\n", empty lines will be generated as newlines without a #
     * You can supply one string with \n or multiple strings.
     */
    public void addCommentAbove(String key, String... comment) {
        List<String> comments = new ArrayList<>();
        for (String s : comment) {
            comments.addAll(Arrays.asList(s.split("\n")));
        }

        this.comments.add(new ConfigComment(key, comments, true));
        checkAutoSave();
    }

    /**
     * Adds a comment inline with the specified key
     * @param key The key to add the comment inline with
     * @param comment The comment to add (will be formatted like "# {comment}")
     */
    public void addCommentInline(String key, String comment) {
        comments.add(new ConfigComment(key, Collections.singletonList(comment), false));
        checkAutoSave();
    }

    // This is essentially a debounce system. It will only save after 1 second. So if 100 addComment methods are
    // called in the constructor, it will save only once after 1 second.
    private void checkAutoSave() {
        if (thread != null) { return; }
        int size = comments.size();

        thread = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}

            // If the size is the same or more, save it
            if (comments.size() >= size) {
                System.out.println("Saving " + comments.size() + " comments");
                save();
            }
        });
        thread.start();
    }


    // Method to get YamlConfiguration
    public YamlHandler.YamlConfiguration getYamlConfiguration() { return config; }


    // Methods to set values
    public void set(String key, Object value) { config.put(key, value); }
    public void put(String key, Object value) { config.put(key, value); }
    public void putString(String key, String value) { config.put(key, value); }
    public void putBoolean(String key, boolean value) { config.put(key, value); }
    public void putInteger(String key, int value) { config.put(key, value); }
    public void putLong(String key, long value) { config.put(key, value); }
    public void putDouble(String key, double value) { config.putDouble(key, value); }

    // Methods to get values
    public Object get(String key) { return config.get(key); }
    public YamlHandler.ConfigurationSection getConfigurationSection(String key) { return config.getConfigurationSection(key); }
    public String getString(String key) { return config.getString(key); }
    public int getInt(String key) { return config.getInteger(key); }
    public int getInteger(String key) { return config.getInteger(key); }
    public long getLong(String key) { return config.getLong(key); }
    public boolean getBoolean(String key) { return config.getBoolean(key); }
    public List<String> getStringList(String key) { return config.getStringList(key); }
    public double getDouble(String key) { return config.getDouble(key); }
    /**
     * Returns the keys of the config
     * If Deep is enabled, it will dig and find all valid keys that resolve to a value
     * @param deep Whether to search for all sub-keys
     * @return The list of keys found
     */
    public Set<String> getKeys(boolean deep) { return config.getKeys(deep); }
    public boolean contains(String key) { return config.contains(key); }






    // Methods to save and reload
    public void save() {
        try {
            KamiConfigManager.saveKamiConfig(this);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void reload() {
        try {
            config = yamlHandler.loadConfig(true);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
