package com.kamikazejam.kamicommon.nms.event;

import com.kamikazejam.kamicommon.nms.abstraction.event.PreSpawnSpawnerEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;

public class PreSpawnSpawnerAdapter_1_12_R1 implements Listener {

    @EventHandler
    public void onSpawn(SpawnerSpawnEvent e) {
        PreSpawnSpawnerEvent preEvent = new PreSpawnSpawnerEvent(e.getSpawner().getBlock(), e.getEntityType(), e.getLocation());
        preEvent.setCancelled(e.isCancelled());
        Bukkit.getPluginManager().callEvent(preEvent);
        e.setCancelled(preEvent.isCancelled());
    }
}
