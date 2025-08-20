package com.kamikazejam.kamicommon.configuration.standalone;

import com.kamikazejam.kamicommon.yaml.AbstractYamlConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.ApiStatus.Internal;

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

    /**
     * Saves the config to the file
     * @param force If the config should be saved even if no changes were made
     * @return IFF the config was saved (can be skipped if no changes were made & force is false)
     */
    public boolean save(boolean force) {
        return getYamlConfiguration().save(force);
    }

    /**
     * @return If this config has detected changes to its structure that need saving
     */
    public boolean isChanged() {
        return getYamlConfiguration().isChanged();
    }

    @Internal
    public void setChanged(boolean changed) {
        getYamlConfiguration().setChanged(changed);
    }
}
