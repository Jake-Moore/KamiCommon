package com.kamikazejam.kamicommon.library.worldguard;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class WorldGuardHook {
    private static @Nullable WorldGuardApi worldguard = null;
    @Nullable
    public static WorldGuardApi get() {
        if (worldguard != null) {
            return worldguard;
        }

        // Ensure WorldGuard is enabled
        Plugin wg = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if (wg == null || !wg.isEnabled()) { return null; }

        // Check for supported versions
        String ver = wg.getDescription().getVersion();
        if (ver.startsWith("6")) {
            return worldguard = new WorldGuard6(wg, NmsAPI.getNmsWorldWrapper());
        }else if (ver.startsWith("7")) {
            return worldguard = new WorldGuard7(wg, NmsAPI.getNmsWorldWrapper());
        }else {
            return null;
        }
    }
}
