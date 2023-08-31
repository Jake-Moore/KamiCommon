package com.kamikazejamplugins.kamicommon.configuration.config;

import com.kamikazejamplugins.kamicommon.configuration.config.data.ConfigComment;
import com.kamikazejamplugins.kamicommon.yaml.ConfigurationSection;
import com.kamikazejamplugins.kamicommon.yaml.MemoryConfiguration;
import com.kamikazejamplugins.kamicommon.yaml.YamlConfiguration;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.*;


/**
 * A class that represents a configuration file <p>
 * Key methods are {@link AbstractConfig#addCommentAbove(String, String...)} and {@link AbstractConfig#addCommentInline(String, String)} <p>
 * This is an extension of a YamlConfiguration, so all get, set, and put methods are available. <p>
 * <p></p>
 * When extending this class, provide the File to the config in the super, and then add all desired comments <p>
 * Then you can use this object just like a YamlConfiguration, it has all the same methods plus {@link AbstractConfig#save()} and {@link AbstractConfig#reload()} <p>
 */
@SuppressWarnings("unused")
public abstract class AbstractConfig extends ConfigurationSection {
    // Key, Comment
    @Getter private final List<ConfigComment> comments = new ArrayList<>();
    private Thread thread = null;
    @Getter @Setter private boolean defaultCommentsOverwrite = true;

    /**
     * @return The file associated with this config
     */
    protected abstract File getFile();

    /**
     * @return The YamlConfiguration associated with this config
     */
    protected abstract YamlConfiguration getYamlConfiguration();

    /**
     * @return If the config should add defaults
     */
    protected abstract boolean isAddDefaults();

    /**
     * Reloads the config from the file
     */
    public abstract void reload();



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

    /**
     * Saves the config to the file
     */
    public void save() {
        try {
            KamiConfigManager.saveKamiConfig(this);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }






    // Below this point are overrides for the ConfigurationSection abstract class
    // This allows this class to be used as a swap in replacement for a YamlConfiguration (same methods)

    @Override public void set(String key, Object value) { getYamlConfiguration().put(key, value); }
    @Override public void setItemStack(String key, Object itemStack) { getYamlConfiguration().setItemStack(key, itemStack); }
    @Override
    public void setItemBuilder(String key, Object builder) { getYamlConfiguration().setItemBuilder(key, builder); }
    @Override public void put(String key, Object value) { getYamlConfiguration().put(key, value); }
    @Override public void putString(String key, String value) { getYamlConfiguration().put(key, value); }
    @Override public void putBoolean(String key, boolean value) { getYamlConfiguration().put(key, value); }

    @Override public void putByte(String key, byte value) { getYamlConfiguration().putByte(key, value); }

    @Override public void putShort(String key, short value) { getYamlConfiguration().putShort(key, value); }

    @Override public void putInteger(String key, int value) { getYamlConfiguration().put(key, value); }
    @Override public void putInt(String key, int value) { getYamlConfiguration().put(key, value); }
    @Override public void putLong(String key, long value) { getYamlConfiguration().put(key, value); }
    @Override public void putDouble(String key, double value) { getYamlConfiguration().putDouble(key, value); }

    @Override public void putFloat(String key, float value) { getYamlConfiguration().put(key, value); }

    @Override public void setString(String key, String value) { getYamlConfiguration().setString(key, value); }
    @Override public void setBoolean(String key, boolean value) { getYamlConfiguration().setBoolean(key, value); }

    @Override public void setByte(String key, byte value) { getYamlConfiguration().setByte(key, value); }

    @Override public void setShort(String key, short value) { getYamlConfiguration().setShort(key, value); }

    @Override public void setInteger(String key, int value) { getYamlConfiguration().setInteger(key, value);}
    @Override public void setInt(String key, int value) { getYamlConfiguration().setInteger(key, value);}
    @Override public void setLong(String key, long value) { getYamlConfiguration().setLong(key, value); }
    @Override public void setDouble(String key, double value) { getYamlConfiguration().setDouble(key, value); }

    @Override public void setFloat(String key, float value) { getYamlConfiguration().setFloat(key, value); }

    // Methods to get values
    @Override public Object get(String key) { return getYamlConfiguration().get(key); }
    @Override public Object get(String key, Object def) { return getYamlConfiguration().get(key, def); }
    @Override public MemoryConfiguration getConfigurationSection(String key) { return getYamlConfiguration().getConfigurationSection(key); }

    @Override public String getString(String key) { return getYamlConfiguration().getString(key); }
    @Override public String getString(String key, String def) { return getYamlConfiguration().getString(key, def); }
    @Override public boolean isString(String key) { return getYamlConfiguration().isString(key); }

    @Override public int getInt(String key) { return getYamlConfiguration().getInt(key); }
    @Override public int getInt(String key, int def) { return getYamlConfiguration().getInt(key, def); }
    @Override public boolean isInt(String key) { return getYamlConfiguration().isInt(key); }

    @Override public long getLong(String key) { return getYamlConfiguration().getLong(key); }
    @Override public long getLong(String key, long def) { return getYamlConfiguration().getLong(key, def); }
    @Override public boolean isLong(String key) { return getYamlConfiguration().isLong(key); }

    @Override public List<?> getList(String key) { return getYamlConfiguration().getList(key); }
    @Override public List<?> getList(String path, List<?> def) { return getYamlConfiguration().getList(path, def); }
    @Override public boolean isList(String key) { return getYamlConfiguration().isList(key); }

    @Override public boolean getBoolean(String key) { return getYamlConfiguration().getBoolean(key); }
    @Override public boolean getBoolean(String key, boolean def) { return getYamlConfiguration().getBoolean(key, def); }
    @Override public boolean isBoolean(String key) { return getYamlConfiguration().isBoolean(key); }

    @Override public List<String> getStringList(String key) { return getYamlConfiguration().getStringList(key); }
    @Override public List<String> getStringList(String key, List<String> def) { return getYamlConfiguration().getStringList(key, def); }

    @Override public List<Integer> getIntegerList(String key) { return getYamlConfiguration().getIntegerList(key); }
    @Override public List<Integer> getIntegerList(String key, List<Integer> def) { return getYamlConfiguration().getIntegerList(key, def); }

    @Override public List<Byte> getByteList(String key) { return getYamlConfiguration().getByteList(key); }
    @Override public List<Byte> getByteList(String key, List<Byte> def) { return getYamlConfiguration().getByteList(key, def); }

    @Override public double getDouble(String key) { return getYamlConfiguration().getDouble(key); }
    @Override public double getDouble(String key, double def) { return getYamlConfiguration().getDouble(key, def); }
    @Override public boolean isDouble(String key) { return getYamlConfiguration().isDouble(key); }

    @Override public byte getByte(String key) { return getYamlConfiguration().getByte(key); }
    @Override public byte getByte(String key, byte def) { return getYamlConfiguration().getByte(key, def); }
    @Override public boolean isByte(String key) { return getYamlConfiguration().isByte(key); }

    @Override public short getShort(String key) { return getYamlConfiguration().getShort(key); }
    @Override public short getShort(String key, short def) { return getYamlConfiguration().getShort(key, def); }
    @Override public boolean isShort(String key) { return getYamlConfiguration().isShort(key); }

    @Override public float getFloat(String key) { return getYamlConfiguration().getFloat(key); }
    @Override public float getFloat(String key, float def) { return getYamlConfiguration().getFloat(key, def); }
    @Override public boolean isFloat(String key) { return getYamlConfiguration().isFloat(key); }

    @Override public Object getItemStack(String key) { return getYamlConfiguration().getItemStack(key); }
    @Override public Object getItemStack(String key, Object def) { return getYamlConfiguration().getItemStack(key, def); }

    /**
     * Returns the keys of the config
     * If Deep is enabled, it will dig and find all valid keys that resolve to a value
     * @param deep Whether to search for all sub-keys
     * @return The list of keys found
     */
    @Override public Set<String> getKeys(boolean deep) { return getYamlConfiguration().getKeys(deep); }

    @Override public boolean isConfigurationSection(String key) { return getYamlConfiguration().isConfigurationSection(key); }

    @Override public boolean contains(String key) { return getYamlConfiguration().contains(key); }
    @Override public boolean isSet(String key) { return getYamlConfiguration().isSet(key); }

    @Override
    public void addDefault(String key, Object o) { getYamlConfiguration().addDefault(key, o); }

    @Override
    public boolean isEmpty() {
        return getYamlConfiguration().isEmpty();
    }

}
