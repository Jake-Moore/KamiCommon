package com.kamikazejam.kamicommon.library.worldedit;

import com.kamikazejam.kamicommon.library.worldedit.WorldEditApi;
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
        }else {
            return null;
        }
    }
}
