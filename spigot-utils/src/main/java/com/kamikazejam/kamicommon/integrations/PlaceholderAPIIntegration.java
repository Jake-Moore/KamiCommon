package com.kamikazejam.kamicommon.integrations;

import com.kamikazejam.kamicommon.KamiPlugin;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class PlaceholderAPIIntegration implements Listener {
    public PlaceholderAPIIntegration(KamiPlugin plugin) {
        plugin.registerListener(this);
    }

    public String setPlaceholders(@Nullable OfflinePlayer player, String s) {
        return PlaceholderAPI.setPlaceholders(player, s);
    }
}
