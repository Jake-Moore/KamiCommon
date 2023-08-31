package com.kamikazejamplugins.kamicommon.configuration.config;

import com.kamikazejamplugins.kamicommon.yaml.YamlConfiguration;
import com.kamikazejamplugins.kamicommon.yaml.handler.YamlHandler;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.InputStream;
import java.util.function.Supplier;


/**
 * A class that represents a configuration file (Meant for implementations WITH a JavaPlugin object available) <p>
 * If you DO NOT have a JavaPlugin object, it is recommended to use {@link StandaloneConfig} instead <p>
 * Key methods are {@link KamiConfig#addCommentAbove(String, String...)} and {@link KamiConfig#addCommentInline(String, String)} <p>
 * This is an extension of a YamlConfiguration, so all get, set, and put methods are available. <p>
 * <p></p>
 * When extending this class, provide the File to the config in the super, and then add all desired comments <p>
 * Then you can use this object just like a YamlConfiguration, it has all the same methods plus {@link KamiConfig#save()} and {@link KamiConfig#reload()} <p>
 */
@SuppressWarnings("unused")
public class KamiConfig extends AbstractConfig {
    private final JavaPlugin plugin;
    private final File file;
    private final YamlHandler yamlHandler;
    private YamlConfiguration config;
    private final boolean addDefaults;
    private final @Nullable Supplier<InputStream> defaultSupplier;

    public KamiConfig(@Nonnull JavaPlugin plugin, File file) {
        this(plugin, file, true);
    }

    public KamiConfig(@Nonnull JavaPlugin plugin, File file, boolean addDefaults) {
        this(plugin, file, addDefaults, null);
    }

    public KamiConfig(@Nonnull JavaPlugin plugin, File file, Supplier<InputStream> defaultSupplier) {
        this(plugin, file, true, defaultSupplier);
    }

    private KamiConfig(@Nonnull JavaPlugin plugin, File file, boolean addDefaults, @Nullable Supplier<InputStream> defaultSupplier) {
        this.plugin = plugin;
        this.file = file;
        this.addDefaults = true;
        this.defaultSupplier = defaultSupplier;

        ensureFile();

        this.yamlHandler = new YamlHandler(this, plugin, file);
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
    protected File getFile() {
        return file;
    }

    @Override
    protected YamlConfiguration getYamlConfiguration() {
        return config;
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
            plugin.getLogger().severe("[KamiCommon] Failed to create file: " + file.getAbsolutePath());
        }
    }
}
