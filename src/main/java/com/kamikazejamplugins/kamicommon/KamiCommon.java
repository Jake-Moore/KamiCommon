package com.kamikazejamplugins.kamicommon;

import com.kamikazejamplugins.kamicommon.gui.MenuManager;
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

        getServer().getPluginManager().registerEvents(this, this);

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
