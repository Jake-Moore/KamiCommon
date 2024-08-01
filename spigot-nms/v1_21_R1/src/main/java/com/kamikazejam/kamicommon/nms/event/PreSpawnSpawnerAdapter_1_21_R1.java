package com.kamikazejam.kamicommon.nms.event;

import com.destroystokyo.paper.event.entity.PreSpawnerSpawnEvent;
import com.kamikazejam.kamicommon.nms.abstraction.event.PreSpawnSpawnerEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PreSpawnSpawnerAdapter_1_21_R1 implements Listener {
    @EventHandler
    public void onSpawn(PreSpawnerSpawnEvent event) {
        final PreSpawnSpawnerEvent preEvent = new PreSpawnSpawnerEvent(event.getSpawnerLocation().getBlock(), event.getType());
        preEvent.setCancelled(event.isCancelled());
        Bukkit.getServer().getPluginManager().callEvent(preEvent);
        event.setCancelled(preEvent.isCancelled());
    }
}
