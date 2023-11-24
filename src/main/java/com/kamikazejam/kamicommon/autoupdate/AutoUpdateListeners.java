package com.kamikazejam.kamicommon.autoupdate;

import com.kamikazejam.kamicommon.KamiCommon;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
class AutoUpdateListeners implements Listener {
    private final JavaPlugin plugin;
    public AutoUpdateListeners(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("kamicommon.autoupdate.manage") && AutoUpdate.hasBeenUpdated()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    AutoUpdate.notify(player);
                }
            }.runTaskLater(KamiCommon.get(), 40L);
        }
    }

    @EventHandler
    public void onDisable(PluginDisableEvent event) {
        if (event.getPlugin().getName().equals(KamiCommon.get().getName())) {
            AutoUpdate.updateNow(KamiCommon.get());
        }
    }
}
