package com.kamikazejam.kamicommon.menu.loaders;

import com.kamikazejam.kamicommon.SpigotUtilsSource;
import com.kamikazejam.kamicommon.menu.struct.size.MenuSize;
import com.kamikazejam.kamicommon.menu.struct.size.MenuSizeRows;
import com.kamikazejam.kamicommon.menu.struct.size.MenuSizeType;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

public class MenuSizeLoader {
    @NotNull
    public static MenuSize load(@NotNull ConfigurationSection section, @NotNull String key) {
        return load(section.getConfigurationSection(key));
    }
    @NotNull
    public static MenuSize load(@NotNull ConfigurationSection section) {
        // Load Type with higher priority (if successful -> return)
        if (section.isString("type")) {
            String s = section.getString("type");
            try {
                InventoryType type = InventoryType.valueOf(s);
                return new MenuSizeType(type);
            }catch (IllegalArgumentException e) {
                // ignore error, in case there is a valid row count to use instead
            }
        }

        // Try to load rows (may throw IllegalArgumentException for invalid rows value)
        if (section.isInt("rows")) {
            return new MenuSizeRows(section.getInt("rows")); // Runs precondition internally
        }
        if (section.isInt("row")) {
            return new MenuSizeRows(section.getInt("row")); // Runs precondition internally
        }

        // If there is a type string, then we failed to load it properly -> notify about this
        if (section.isString("type")) {
            SpigotUtilsSource.warning("Invalid inventory type in config: '" + section.getString("type") + "' at " + (section.getCurrentPath() + ".type"));
        }
        throw new IllegalStateException("Invalid inventory size (rows or type) in config at " + section.getCurrentPath());
    }
}
