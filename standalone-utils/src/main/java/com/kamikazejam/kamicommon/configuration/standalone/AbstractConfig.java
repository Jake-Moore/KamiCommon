package com.kamikazejam.kamicommon.configuration.standalone;

import com.kamikazejam.kamicommon.yaml.AbstractYamlConfiguration;
import lombok.Getter;
import lombok.Setter;

import java.io.File;


/**
 * A class that represents a configuration file <br>
 * This is an extension of a YamlConfiguration, so all get, set, and put methods are available. <br>
 * <br>
 * When extending this class, provide the File to the config in the super, and then add all desired comments <br>
 * Then you can use this object just like a YamlConfiguration, it has all the same methods plus {@link AbstractConfig#save()} and {@link AbstractConfig#reload()} <br>
 */
@Setter
@Getter
@SuppressWarnings("unused")
public abstract class AbstractConfig<T extends AbstractYamlConfiguration> {
    private boolean defaultCommentsOverwrite = true;

    /**
     * @return The file associated with this config
     */
    protected abstract File getFile();

    /**
     * @return The YamlConfiguration associated with this config
     */
    protected abstract T getYamlConfiguration();

    /**
     * @return If the config should add defaults
     */
    protected abstract boolean isAddDefaults();

    /**
     * Reloads the config from the file
     */
    public abstract void reload();

    /**
     * Saves the config to the file
     * @return IFF the config was saved (can be skipped if no changes were made)
     */
    public boolean save() {
        return getYamlConfiguration().save();
    }

}
