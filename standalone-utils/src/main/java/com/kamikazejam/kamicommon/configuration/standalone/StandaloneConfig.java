package com.kamikazejam.kamicommon.configuration.standalone;

import com.kamikazejam.kamicommon.yaml.standalone.ConfigurationSectionStandalone;
import com.kamikazejam.kamicommon.yaml.standalone.MemorySectionStandalone;
import com.kamikazejam.kamicommon.yaml.standalone.YamlConfigurationStandalone;
import com.kamikazejam.kamicommon.yaml.standalone.YamlHandlerStandalone;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A class that represents a configuration file (Meant for implementations WITHOUT a JavaPlugin object available) <p>
 * If you have a JavaPlugin object, it is recommended to use @KamiConfig instead <p>
 * This is an extension of a YamlConfiguration, so all get, set, and put methods are available. <p>
 * <p></p>
 * IF extending this class, provide the File to the config in the super, and then add all desired comments <p>
 * Then you can use this object just like a YamlConfiguration, it has all the same methods plus {@link StandaloneConfig#save()} and {@link StandaloneConfig#reload()} <p>
 */
@SuppressWarnings("unused")
public class StandaloneConfig extends AbstractConfig<YamlConfigurationStandalone> implements ConfigurationSectionStandalone {
    private final File file;
    private final YamlHandlerStandalone yamlHandler;
    private YamlConfigurationStandalone config;
    private final boolean addDefaults;
    private final @Nullable Supplier<InputStream> defaultSupplier;

    public StandaloneConfig(File file) {
        this(file, true);
    }

    public StandaloneConfig(File file, boolean addDefaults) {
        this(file, addDefaults, null);
    }

    public StandaloneConfig(File file, Supplier<InputStream> defaultStream) {
        this(file, true, defaultStream);
    }

    public StandaloneConfig(File file, boolean addDefaults, @Nullable Supplier<InputStream> defaultStream) {
        this.file = file;
        this.addDefaults = addDefaults;
        this.defaultSupplier = defaultStream;

        ensureFile();

        this.yamlHandler = new YamlHandlerStandalone(this, file);
        this.config = yamlHandler.loadConfig(addDefaults, defaultSupplier);
        save();
    }

    @Override
    public void reload() {
        try {
            config = yamlHandler.loadConfig(addDefaults, defaultSupplier);
            save();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected YamlConfigurationStandalone getYamlConfiguration() { return config; }

    @Override
    protected File getFile() {
        return file;
    }

    @Override
    protected boolean isAddDefaults() {
        return addDefaults;
    }

    private void ensureFile() {
        // Ensure the file exists
        try {
            if (!file.exists() && !file.getParentFile().mkdirs() && !file.createNewFile()) {
                throw new Exception("Failed to create file: " + file.getAbsolutePath());
            }
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("[KamiCommon] Failed to create file: " + file.getAbsolutePath());
        }
    }















    // Below this point are overrides for the ConfigurationSection abstract class
    // This allows this class to be used as a swap in replacement for a YamlConfiguration (same methods)
    @Override public void set(String key, Object value) { put(key, value); }
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
    @Override public @NotNull MemorySectionStandalone getConfigurationSection(String key) { return getYamlConfiguration().getConfigurationSection(key); }

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

    @Override public BigDecimal getBigDecimal(String key) { return getYamlConfiguration().getBigDecimal(key); }
    @Override public BigDecimal getBigDecimal(String key, BigDecimal def) { return getYamlConfiguration().getBigDecimal(key, def); }
    @Override public boolean isNumber(String key) { return getYamlConfiguration().isNumber(key); }

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

    @Override public void addDefault(String key, Object o) { getYamlConfiguration().addDefault(key, o); }
    @Override
    public boolean isEmpty() { return getYamlConfiguration().isEmpty(); }

    @Override
    public String getCurrentPath() {
        // No current path, this is root config
        return "";
    }
}