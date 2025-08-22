package com.kamikazejam.kamicommon.configuration.spigot;

import com.kamikazejam.kamicommon.KamiPlugin;
import com.kamikazejam.kamicommon.configuration.spigot.observe.ConfigObserver;
import com.kamikazejam.kamicommon.configuration.spigot.observe.ObservableConfig;
import com.kamikazejam.kamicommon.configuration.standalone.AbstractConfig;
import com.kamikazejam.kamicommon.configuration.standalone.StandaloneConfig;
import com.kamikazejam.kamicommon.item.IBuilder;
import com.kamikazejam.kamicommon.subsystem.AbstractSubsystem;
import com.kamikazejam.kamicommon.util.log.JavaPluginLogger;
import com.kamikazejam.kamicommon.util.log.LoggerService;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSequenceSpigot;
import com.kamikazejam.kamicommon.yaml.spigot.MemorySection;
import com.kamikazejam.kamicommon.yaml.spigot.YamlConfiguration;
import com.kamikazejam.kamicommon.yaml.spigot.YamlHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;


/**
 * A class that represents a configuration file (Meant for implementations WITH a JavaPlugin object available) <br>
 * If you DO NOT have a JavaPlugin object, it is recommended to use {@link StandaloneConfig} instead <br>
 * This is an extension of a YamlConfiguration, so all get, set, and put methods are available. <br>
 * <br>
 * When extending this class, provide the File to the config in the super, and then add all desired comments <br>
 * Then you can use this object just like a YamlConfiguration, it has all the same methods plus a few others like {@link KamiConfig#reload()} <br>
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class KamiConfig extends AbstractConfig<YamlConfiguration> implements ConfigurationSection, ObservableConfig {
    private final @NotNull LoggerService logger;
    private final File file;
    private final YamlHandler yamlHandler;
    private YamlConfiguration config;
    private final @NotNull Set<ConfigObserver> observers = new HashSet<>();

    // -------------------------------------------------- //
    //               JavaPlugin Constructors              //
    // -------------------------------------------------- //

    /**
     * Creates a new config instance with the given plugin and destination file.<br><br>
     * This constructor enables defaults using the following resource file method:<br>
     * - Assumes a resource file with the same name as the provided file, exists in the current jar.
     */
    public KamiConfig(@NotNull JavaPlugin plugin, File file) {
        this(plugin, file, () -> plugin.getResource(file.getName()));
    }

    /**
     * Creates a new config instance with the given plugin and destination file.<br><br>
     * This constructor uses defaults if and only if the provided supplier is NOT null:<br>
     * - Providing a non-null supplier will enable defaults using the provided InputStream
     * - Providing a null supplier will disable defaults
     *
     * @param defaultsStream The optional supplier to load defaults from.
     */
    public KamiConfig(@NotNull JavaPlugin plugin, File file, @Nullable Supplier<InputStream> defaultsStream) {
        this(parseLogger(plugin), file, defaultsStream);
    }

    // -------------------------------------------------- //
    //                Subsystem Constructors              //
    // -------------------------------------------------- //

    /**
     * Creates a new config instance with the given subsystem and destination file.<br><br>
     * This constructor enables defaults using the following resource file method:<br>
     * - Fetches the resource file using the provided file name, from {@link AbstractSubsystem#getSupplementalConfigResource(String)}
     */
    public KamiConfig(@NotNull AbstractSubsystem<?, ?> subsystem, File file) {
        this(subsystem, file, () -> subsystem.getSupplementalConfigResource(file.getName()));
    }

    /**
     * Creates a new config instance with the given subsystem and destination file.<br><br>
     * This constructor uses defaults if and only if the provided supplier is NOT null:<br>
     * - Providing a non-null supplier will enable defaults using the provided InputStream
     * - Providing a null supplier will disable defaults
     */
    public KamiConfig(@NotNull AbstractSubsystem<?, ?> subsystem, File file, @Nullable Supplier<InputStream> defaultsStream) {
        this((LoggerService) subsystem, file, defaultsStream);
    }

    // -------------------------------------------------- //
    //                 Internal Constructors              //
    // -------------------------------------------------- //

    /**
     * @param file The target file on the destination filesystem to save and load config data from.
     * @param defaultsStream The optional supplier to load defaults from.
     */
    private KamiConfig(@NotNull LoggerService logger, @NotNull File file, @Nullable Supplier<InputStream> defaultsStream) {
        this.logger = logger;
        this.file = file;

        ensureFile();

        this.yamlHandler = new YamlHandler(this, logger, file, defaultsStream);
        this.config = yamlHandler.loadConfig();
        save(true); // Force save since there won't be any changes from load, but we want to write any new comments to file
    }

    @Override
    public void reload() {
        try {
            config = yamlHandler.loadConfig();
            save();
            observers.forEach(observer -> observer.onConfigLoaded(this));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected File getFile() {
        return file;
    }

    @Override
    protected YamlConfiguration getYamlConfiguration() {
        return config;
    }

    private void ensureFile() {
        // Ensure the file exists
        try {
            if (!file.exists() && !file.getParentFile().mkdirs() && !file.createNewFile()) {
                throw new Exception("Failed to create file: " + file.getAbsolutePath());
            }
        }catch (Exception e) {
            e.printStackTrace();
            logger.severe("[KamiCommon] Failed to create file: " + file.getAbsolutePath());
        }
    }

    @Override
    public boolean registerConfigObserver(@NotNull ConfigObserver observer) {
        if (observers.add(observer)) {
            // Call the observer immediately, since the config has already been loaded
            observer.onConfigLoaded(this);
            return true;
        }
        return false;
    }

    @Override
    public void unregisterConfigObserver(@NotNull ConfigObserver observer) {
        observers.remove(observer);
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
    @Override public @NotNull MemorySection getConfigurationSection(String key) { return getYamlConfiguration().getConfigurationSection(key); }
    @Override public @NotNull ConfigurationSequenceSpigot getConfigurationSequence(String key) { return getYamlConfiguration().getConfigurationSequence(key); }

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
    @Override public boolean isEmpty() { return getYamlConfiguration().isEmpty(); }


    // Item Methods
    @Override public ItemStack getItemStack(String key) { return getYamlConfiguration().getItemStack(key); }
    @Override public ItemStack getItemStack(String key, ItemStack def) { return getYamlConfiguration().getItemStack(key, def); }
    @Override public void setItemStack(String key, ItemStack item) { getYamlConfiguration().setItemStack(key, item); }
    @Override public void setItemBuilder(String key, IBuilder builder) { getYamlConfiguration().setItemBuilder(key, builder); }
    @Override public IBuilder getItemBuilder(String key) { return getYamlConfiguration().getItemBuilder(key); }
    @Override public boolean isItemStack(String key) { return getYamlConfiguration().isItemStack(key); }

    @Override
    public String getCurrentPath() {
        // No path, this is the root config
        return "";
    }

    @NotNull
    private static LoggerService parseLogger(@NotNull JavaPlugin plugin) {
        if (plugin instanceof KamiPlugin kp) {
            return kp.getColorLogger();
        } else {
            return new JavaPluginLogger(plugin);
        }
    }
}
