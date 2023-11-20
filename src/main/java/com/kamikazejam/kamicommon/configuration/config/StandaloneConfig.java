package com.kamikazejam.kamicommon.configuration.config;

import com.kamikazejam.kamicommon.yaml.YamlConfiguration;
import com.kamikazejam.kamicommon.yaml.handler.YamlHandlerStandalone;

import javax.annotation.Nullable;
import java.io.File;
import java.io.InputStream;
import java.util.function.Supplier;

/**
 * A class that represents a configuration file (Meant for implementations WITHOUT a JavaPlugin object available) <p>
 * If you have a JavaPlugin object, it is recommended to use {@link KamiConfig} instead <p>
 * This is an extension of a YamlConfiguration, so all get, set, and put methods are available. <p>
 * <p></p>
 * IF extending this class, provide the File to the config in the super, and then add all desired comments <p>
 * Then you can use this object just like a YamlConfiguration, it has all the same methods plus {@link StandaloneConfig#save()} and {@link StandaloneConfig#reload()} <p>
 */
@SuppressWarnings("unused")
public class StandaloneConfig extends AbstractConfig {
    private final File file;
    private final YamlHandlerStandalone yamlHandler;
    private YamlConfiguration config;
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

        long ms = System.currentTimeMillis();
        this.yamlHandler = new YamlHandlerStandalone(this, file);
        System.out.println("YamlHandler took: " + (System.currentTimeMillis() - ms) + " ms."); ms = System.currentTimeMillis();

        System.out.println("Loading config...");
        this.config = yamlHandler.loadConfig(addDefaults, defaultSupplier);
        System.out.println("Config took: " + (System.currentTimeMillis() - ms) + " ms."); ms = System.currentTimeMillis();

        boolean b = save();
        System.out.println("Save (" + b + ") took: " + (System.currentTimeMillis() - ms) + " ms.");
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
    protected YamlConfiguration getYamlConfiguration() { return config; }

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
}