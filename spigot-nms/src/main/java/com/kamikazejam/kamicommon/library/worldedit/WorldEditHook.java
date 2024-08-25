package com.kamikazejam.kamicommon.library.worldedit;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class WorldEditHook {
    private static WorldEditApi<Clipboard> worldEditVer = null;

    @Nullable
    public static WorldEditApi<Clipboard> get() {
        if (worldEditVer != null) {
            return worldEditVer;
        }

        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (plugin == null || !plugin.isEnabled()) { return null; }

        String ver = plugin.getDescription().getVersion();
        if (ver.startsWith("6")) {
            return worldEditVer = new WorldEdit6();
        } else if (ver.startsWith("7")) {
            return worldEditVer = new WorldEdit7();
        }

        // Try to fetch FAWE (for later versions WorldEdit is not required to run FAWE)
        // So if we find FAWE without WE, we can assume WE 7
        Plugin fawe = Bukkit.getServer().getPluginManager().getPlugin("FastAsyncWorldEdit");
        if (fawe != null && fawe.isEnabled()) {
            try {
                // If this class is found, it's v7 WorldEdit within FAWE
                Class<?> v7Specific = Class.forName("com.sk89q.worldedit.math.BlockVector3");
                return worldEditVer = new WorldEdit7();
            }catch (Throwable ignored) {
                // If we have an error, try falling back to WE 6
                return worldEditVer = new WorldEdit6();
            }
        }

        return null;
    }
}
