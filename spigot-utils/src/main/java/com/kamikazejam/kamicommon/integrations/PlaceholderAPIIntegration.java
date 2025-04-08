package com.kamikazejam.kamicommon.integrations;

import com.kamikazejam.kamicommon.KamiPlugin;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class PlaceholderAPIIntegration implements Listener {
    public PlaceholderAPIIntegration(KamiPlugin plugin) {
        plugin.registerListeners(this);
    }

    @SuppressWarnings("ConstantValue")
    public String setPlaceholders(@Nullable OfflinePlayer player, String s) {
        // In very rare cases on shutdown this can return null. If it does just return the string w/out placeholders
        if (PlaceholderAPIPlugin.getInstance() == null) return s;
        return PlaceholderAPI.setPlaceholders(player, s);
    }
}
