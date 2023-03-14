package com.kamikazejamplugins.kamicommon;

import com.kamikazejamplugins.kamicommon.gui.MenuManager;
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
        Bukkit.getLogger().info("KamiCommon enabling...");

        plugin = this;
        plugin.getServer().getPluginManager().registerEvents(new MenuManager(), plugin);
        getServer().getPluginManager().registerEvents(this, this);

        if (isWineSpigot()) {
            getLogger().info("WineSpigot (1.8.8) detected!");
        }

        if (System.getProperty("KAMICOMMON_ALREADY_LOADED", "").equalsIgnoreCase("TRUE")) {
            getLogger().severe("KamiCommon is already loaded! This is not supported!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        System.setProperty("KAMICOMMON_ALREADY_LOADED", "TRUE");
        Bukkit.getLogger().info("KamiCommon enabled in " + (System.currentTimeMillis() - start) + "ms");
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

//    @EventHandler
//    public void onShift(PlayerToggleSneakEvent event) {
//        if (event.getPlayer().isSneaking()) { return; }
//        if (event.getPlayer().isFlying()) { return; }
//
//        Location loc1 = event.getPlayer().getLocation().clone().add(0, 0, 3);
//        Location loc2 = event.getPlayer().getLocation().clone().add(0, 0, -3);
//
//        NmsManager.getBlockUtil().setBlockSuperFast(loc1.getBlock(), Material.WATER, false, false);
//        NmsManager.getBlockUtil().setBlockSuperFast(loc2.getBlock(), new MaterialData(Material.WATER, (byte) 8), false, false);
//    }
}
