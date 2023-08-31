package com.kamikazejamplugins.kamicommon.gui.items;

import com.kamikazejamplugins.kamicommon.item.IAItemBuilder;
import com.kamikazejamplugins.kamicommon.item.IBuilder;
import com.kamikazejamplugins.kamicommon.gui.interfaces.MenuClickInfo;
import com.kamikazejamplugins.kamicommon.yaml.ConfigurationSection;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nullable;

@Getter
@SuppressWarnings("unused")
public class KamiMenuItem {
    private final boolean enabled;
    private final IBuilder iBuilder;
    private final int slot;
    @Setter @Nullable private MenuClickInfo clickInfo = null;

    // For like KamiMenuItem(config, "menus.menu1")
    public KamiMenuItem(ConfigurationSection section, String key) {
        this(section.getConfigurationSection(key));
    }
    public KamiMenuItem(ConfigurationSection section, String key, OfflinePlayer player) {
        this(section.getConfigurationSection(key));
    }



    // For calls like KamiMenuItem(config.getConfigurationSection("menus.menu1"))
    public KamiMenuItem(ConfigurationSection section) {
        this(section, (OfflinePlayer) null);
    }

    /**
     * @param player Only required for PLAYER_HEAD ItemStacks, player is used as the skullOwner
     */
    public KamiMenuItem(ConfigurationSection section, @Nullable OfflinePlayer player) {
        enabled = section.getBoolean("enabled", true);
        iBuilder = new IAItemBuilder(section, player); // Null safe for player arg
        slot = section.getInt("slot", -1);
    }
}
