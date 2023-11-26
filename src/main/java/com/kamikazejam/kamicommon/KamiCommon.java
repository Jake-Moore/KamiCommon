package com.kamikazejam.kamicommon;

import com.kamikazejam.kamicommon.gui.MenuManager;
import com.kamikazejam.kamicommon.gui.MenuTask;
import com.kamikazejam.kamicommon.yaml.standalone.YamlUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class KamiCommon extends JavaPlugin implements Listener {
    private static KamiCommon plugin;

    @Override
    public void onEnable(){
        long start = System.currentTimeMillis();
        getLogger().info("KamiCommon enabling...");

        plugin = this;
        plugin.getServer().getPluginManager().registerEvents(new MenuManager(), plugin);
        getServer().getPluginManager().registerEvents(this, this);

        // Schedule menu task to run every 1 second
        Bukkit.getScheduler().runTaskTimer(this, new MenuTask(), 0L, 20L);

        if (isWineSpigot()) {
            getLogger().info("WineSpigot (1.8.8) detected!");
        }

        // Create Yaml Loader
        getLogger().info("Creating Yaml Loader");
        YamlUtil.getYaml();

        getLogger().info("KamiCommon enabled in " + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll((Plugin) plugin);
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


    private static Boolean hasItemsAdder = null;
    public static boolean hasItemsAdder() {
        if (hasItemsAdder == null) {
            return hasItemsAdder = Bukkit.getPluginManager().getPlugin("ItemsAdder") != null;
        }
        return hasItemsAdder;
    }
}
