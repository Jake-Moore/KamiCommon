package com.kamikazejamplugins.kamicommon.config.data;

import com.kamikazejamplugins.kamicommon.config.KamiConfigManager;
import com.kamikazejamplugins.kamicommon.yaml.YamlHandler;
import lombok.Getter;

import java.io.File;

/**
 * Create any implementation of this class with {@link KamiConfig#create(Class, File)}.
 *  <b>DO NOT USE THE IMPLEMENTATION'S CONSTRUCTOR!!!</b>
 */
@SuppressWarnings("unused")
public abstract class KamiConfig {
    @Getter private File file;
    @Getter YamlHandler.YamlConfiguration cfg;

    public static <T extends KamiConfig> T create(Class<T> configClass, File file) throws Exception {
        KamiConfig config = configClass.newInstance();
        config.setFile(file);
        config.load();
        return configClass.cast(config);
    }

    public void setFile(File file) {
        this.file = file;
        try {
            if (!file.exists() && !file.createNewFile()) {
                throw new Exception("Failed to create file");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the config from the file
     *  And then saves it to update any new comments
     */
    public void load() {
        reload();
        save();
    }

    /**
     * Saves the config to the file.
     */
    public void save() {
        KamiConfigManager.saveKamiConfigToFile(this);
    }

    /**
     * Reloads the config from the file.
     */
    public void reload() {
        this.cfg = KamiConfigManager.loadKamiConfigFromFile(this);
    }
}
