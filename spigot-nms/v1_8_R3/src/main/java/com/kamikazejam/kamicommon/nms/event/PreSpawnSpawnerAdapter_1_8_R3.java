package com.kamikazejam.kamicommon.nms.event;

import com.kamikazejam.kamicommon.nms.abstraction.event.PreSpawnSpawnerEvent;
import net.techcable.tacospigot.event.entity.SpawnerPreSpawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PreSpawnSpawnerAdapter_1_8_R3 implements Listener {
    @EventHandler
    public void onSpawn(SpawnerPreSpawnEvent event) {
        final PreSpawnSpawnerEvent preEvent = new PreSpawnSpawnerEvent(event.getLocation().getBlock(), event.getSpawnedType());
        preEvent.setCancelled(event.isCancelled());
        Bukkit.getPluginManager().callEvent(preEvent);
        event.setCancelled(preEvent.isCancelled());
    }
}
