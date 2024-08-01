package com.kamikazejam.kamicommon.nms.event;

import com.kamikazejam.kamicommon.nms.abstraction.event.PreSpawnSpawnerEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;

public class PreSpawnSpawnerAdapter_1_12_R1 implements Listener {

    @EventHandler
    public void onSpawn(final SpawnerSpawnEvent event) {
        PreSpawnSpawnerEvent preEvent = new PreSpawnSpawnerEvent(event.getLocation().getBlock(), event.getEntityType());
        preEvent.setCancelled(event.isCancelled());
        Bukkit.getPluginManager().callEvent(preEvent);
        event.setCancelled(preEvent.isCancelled());

        if (!event.isCancelled()) { return; }
        event.getSpawner().setSpawnCount(0); // If cancelled -> set spawn count to 0
    }
}
