package com.kamikazejamplugins.kamicommon;

import com.kamikazejamplugins.kamicommon.gui.MenuManager;
import com.kamikazejamplugins.kamicommon.nms.NmsManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class KamiCommon extends JavaPlugin implements Listener {
    private static KamiCommon plugin;

    @Override
    public void onEnable(){
        plugin = this;
        Bukkit.getLogger().info("KamiCommon enabled");
        plugin.getServer().getPluginManager().registerEvents(new MenuManager(), plugin);

        getLogger().info("Full BlockUtil Class: " + NmsManager.getBlockUtil().getClass().getName());
        getLogger().info("IsWineSpigot: " + isWineSpigot());
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("KamiCommon disabled");
    }

    public static JavaPlugin get() {
        return plugin;
    }


    private static Boolean isWineSpigot = null;
    public static boolean isWineSpigot() {
        if (isWineSpigot == null) {
            return isWineSpigot = Bukkit.getServer().getName().equals("WineSpigot");
        }
        return isWineSpigot;
    }
}
