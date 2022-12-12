package com.kamikazejamplugins.kamicommon.config.data;

import com.kamikazejamplugins.kamicommon.config.KamiConfigManager;
import com.kamikazejamplugins.kamicommon.yaml.ConfigurationSection;
import com.kamikazejamplugins.kamicommon.yaml.MemoryConfiguration;
import com.kamikazejamplugins.kamicommon.yaml.YamlConfiguration;
import com.kamikazejamplugins.kamicommon.yaml.YamlHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
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
public abstract class KamiConfig extends ConfigurationSection {
    @Getter private final File file;
    private final YamlHandler yamlHandler;
    private YamlConfiguration config;
    private final boolean addDefaults;

    // Key, Comment
    @Getter private final List<ConfigComment> comments = new ArrayList<>();
    private Thread thread = null;

    public KamiConfig(@Nullable JavaPlugin plugin, File file) {
        this.file = file;
        this.addDefaults = true;

        // Ensure the file exists
        try {
            if (!file.exists() && !file.createNewFile()) {
                throw new Exception("Failed to create file");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        this.yamlHandler = new YamlHandler(plugin, file);
        this.config = yamlHandler.loadConfig(true);
        save();
    }

    public KamiConfig(@Nullable JavaPlugin plugin, File file, boolean addDefaults) {
        this.file = file;
        this.addDefaults = addDefaults;

        // Ensure the file exists
        try {
            if (!file.exists() && !file.createNewFile()) {
                throw new Exception("Failed to create file: " + file.getAbsolutePath());
            }
        }catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().severe("Failed to create file: " + file.getAbsolutePath());
        }

        this.yamlHandler = new YamlHandler(plugin, file);
        this.config = yamlHandler.loadConfig(addDefaults);
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
                //System.out.println("Saving " + comments.size() + " comments");
                save();
            }
        });
        thread.start();
    }

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
            config = yamlHandler.loadConfig(addDefaults);
            save();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Method to get YamlConfiguration
    public YamlConfiguration getYamlConfiguration() { return config; }




    // Below this point are overrides for the ConfigurationSection abstract class
    // This allows this class to be used as a swap in replacement for a YamlConfiguration (same methods)


    @Override public void set(String key, Object value) { config.put(key, value); }
    @Override public void setItemStack(String key, ItemStack itemStack) { config.setItemStack(key, itemStack); }
    @Override public void put(String key, Object value) { config.put(key, value); }
    @Override public void putString(String key, String value) { config.put(key, value); }
    @Override public void putBoolean(String key, boolean value) { config.put(key, value); }

    @Override public void putByte(String key, byte value) { config.putByte(key, value); }

    @Override public void putShort(String key, short value) { config.putShort(key, value); }

    @Override public void putInteger(String key, int value) { config.put(key, value); }
    @Override public void putInt(String key, int value) { config.put(key, value); }
    @Override public void putLong(String key, long value) { config.put(key, value); }
    @Override public void putDouble(String key, double value) { config.putDouble(key, value); }

    @Override public void putFloat(String key, float value) { config.put(key, value); }

    @Override public void setString(String key, String value) { config.setString(key, value); }
    @Override public void setBoolean(String key, boolean value) { config.setBoolean(key, value); }

    @Override public void setByte(String key, byte value) { config.setByte(key, value); }

    @Override public void setShort(String key, short value) { config.setShort(key, value); }

    @Override public void setInteger(String key, int value) { config.setInteger(key, value);}
    @Override public void setInt(String key, int value) { config.setInteger(key, value);}
    @Override public void setLong(String key, long value) { config.setLong(key, value); }
    @Override public void setDouble(String key, double value) { config.setDouble(key, value); }

    @Override public void setFloat(String key, float value) { config.setFloat(key, value); }

    // Methods to get values
    @Override public Object get(String key) { return config.get(key); }
    @Override public Object get(String key, Object def) { return config.get(key, def); }
    @Override public MemoryConfiguration getConfigurationSection(String key) { return config.getConfigurationSection(key); }

    @Override public String getString(String key) { return config.getString(key); }
    @Override public String getString(String key, String def) { return config.getString(key, def); }
    @Override public boolean isString(String key) { return config.isString(key); }

    @Override public int getInt(String key) { return config.getInt(key); }
    @Override public int getInt(String key, int def) { return config.getInt(key, def); }
    @Override public boolean isInt(String key) { return config.isInt(key); }

    @Override public long getLong(String key) { return config.getLong(key); }
    @Override public long getLong(String key, long def) { return config.getLong(key, def); }
    @Override public boolean isLong(String key) { return config.isLong(key); }

    @Override public List<?> getList(String key) { return config.getList(key); }
    @Override public List<?> getList(String path, List<?> def) { return config.getList(path, def); }
    @Override public boolean isList(String key) { return config.isList(key); }

    @Override public boolean getBoolean(String key) { return config.getBoolean(key); }
    @Override public boolean getBoolean(String key, boolean def) { return config.getBoolean(key, def); }
    @Override public boolean isBoolean(String key) { return config.isBoolean(key); }

    @Override public List<String> getStringList(String key) { return config.getStringList(key); }
    @Override public List<String> getStringList(String key, List<String> def) { return config.getStringList(key, def); }

    @Override public List<Integer> getIntegerList(String key) { return config.getIntegerList(key); }
    @Override public List<Integer> getIntegerList(String key, List<Integer> def) { return config.getIntegerList(key, def); }

    @Override public List<Byte> getByteList(String key) { return config.getByteList(key); }
    @Override public List<Byte> getByteList(String key, List<Byte> def) { return config.getByteList(key, def); }

    @Override public double getDouble(String key) { return config.getDouble(key); }
    @Override public double getDouble(String key, double def) { return config.getDouble(key, def); }
    @Override public boolean isDouble(String key) { return config.isDouble(key); }

    @Override public byte getByte(String key) { return config.getByte(key); }
    @Override public byte getByte(String key, byte def) { return config.getByte(key, def); }
    @Override public boolean isByte(String key) { return config.isByte(key); }

    @Override public short getShort(String key) { return config.getShort(key); }
    @Override public short getShort(String key, short def) { return config.getShort(key, def); }
    @Override public boolean isShort(String key) { return config.isShort(key); }

    @Override public float getFloat(String key) { return config.getFloat(key); }
    @Override public float getFloat(String key, float def) { return config.getFloat(key, def); }
    @Override public boolean isFloat(String key) { return config.isFloat(key); }

    @Override public ItemStack getItemStack(String key) { return config.getItemStack(key); }
    @Override public ItemStack getItemStack(String key, ItemStack def) { return config.getItemStack(key, def); }

    /**
     * Returns the keys of the config
     * If Deep is enabled, it will dig and find all valid keys that resolve to a value
     * @param deep Whether to search for all sub-keys
     * @return The list of keys found
     */
    @Override public Set<String> getKeys(boolean deep) { return config.getKeys(deep); }

    @Override public boolean isConfigurationSection(String key) { return config.isConfigurationSection(key); }

    @Override public boolean contains(String key) { return config.contains(key); }
    @Override public boolean isSet(String key) { return config.isSet(key); }

    @Override
    public void addDefault(String key, Object o) { config.addDefault(key, o); }
}
