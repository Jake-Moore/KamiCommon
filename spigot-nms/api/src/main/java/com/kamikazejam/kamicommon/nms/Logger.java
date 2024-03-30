package com.kamikazejam.kamicommon.nms;

import org.bukkit.Bukkit;

@SuppressWarnings("unused")
public class Logger {
    public static void info(String s) {
        // Don't have access to KamiCommon specifically in this module, this works well enough
        Bukkit.getLogger().info("[KamiCommon] " + s);
    }
    public static void warning(String s) {
        // Don't have access to KamiCommon specifically in this module, this works well enough
        Bukkit.getLogger().warning("[KamiCommon] " + s);
    }
    public static void severe(String s) {
        // Don't have access to KamiCommon specifically in this module, this works well enough
        Bukkit.getLogger().severe("[KamiCommon] " + s);
    }
}
