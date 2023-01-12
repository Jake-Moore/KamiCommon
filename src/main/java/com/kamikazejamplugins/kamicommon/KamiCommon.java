package com.kamikazejamplugins.kamicommon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class KamiCommon extends JavaPlugin {
    private static KamiCommon plugin;

    @Override
    public void onEnable(){
        plugin = this;
        Bukkit.getLogger().info("KamiCommon enabled");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("KamiCommon disabled");
    }

    public static JavaPlugin get() {
        return plugin;
    }
}
