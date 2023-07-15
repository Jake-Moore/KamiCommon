package com.kamikazejamplugins.kamicommon.configuration.config;

import com.kamikazejamplugins.kamicommon.yaml.YamlConfiguration;
import com.kamikazejamplugins.kamicommon.yaml.handler.YamlHandlerStandalone;

import java.io.File;

/**
 * A class that represents a configuration file (Meant for implementations WITHOUT a JavaPlugin object available) <p>
 * If you have a JavaPlugin object, it is recommended to use {@link KamiConfig} instead <p>
 * Key methods are {@link StandaloneConfig#addCommentAbove(String, String...)} and {@link StandaloneConfig#addCommentInline(String, String)} <p>
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

    public StandaloneConfig(File file) {
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

        this.yamlHandler = new YamlHandlerStandalone(this, file);
        this.config = yamlHandler.loadConfig(true);
        save();
    }

    public StandaloneConfig(File file, boolean addDefaults) {
        this.file = file;
        this.addDefaults = addDefaults;

        // Ensure the file exists
        try {
            if (!file.exists() && !file.getParentFile().mkdirs() && !file.createNewFile()) {
                throw new Exception("Failed to create file: " + file.getAbsolutePath());
            }
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("[KamiCommon] Failed to create file: " + file.getAbsolutePath());
        }

        this.yamlHandler = new YamlHandlerStandalone(this, file);
        this.config = yamlHandler.loadConfig(addDefaults);
        save();
    }

    @Override
    public void reload() {
        try {
            config = yamlHandler.loadConfig(addDefaults);
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
}