package com.kamikazejam.kamicommon.integrations;

import com.kamikazejam.kamicommon.KamiCommon;
import de.myzelyam.api.vanish.VanishAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.UUID;

@SuppressWarnings("unused")
public class PremiumVanishIntegration implements Listener {
    public PremiumVanishIntegration(KamiCommon plugin) {
        plugin.registerListener(this);
    }

    public boolean isVanished(Player player) {
        return VanishAPI.isInvisible(player);
    }
    public boolean isVanished(UUID uuid) {
        return VanishAPI.isInvisibleOffline(uuid);
    }
    public boolean canSee(Player viewer, Player target) {
        return VanishAPI.canSee(viewer, target);
    }
}
