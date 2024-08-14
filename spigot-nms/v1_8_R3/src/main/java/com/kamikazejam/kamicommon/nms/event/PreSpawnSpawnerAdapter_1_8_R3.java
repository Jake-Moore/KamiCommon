package com.kamikazejam.kamicommon.nms.event;

import com.kamikazejam.kamicommon.nms.abstraction.event.PreSpawnSpawnerEvent;
import net.techcable.tacospigot.event.entity.SpawnerPreSpawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PreSpawnSpawnerAdapter_1_8_R3 implements Listener {
    @EventHandler
    public void onSpawn(SpawnerPreSpawnEvent e) {
        PreSpawnSpawnerEvent preEvent = new PreSpawnSpawnerEvent(e.getLocation().getBlock(), e.getSpawnedType(), null);
        preEvent.setCancelled(e.isCancelled());
        Bukkit.getPluginManager().callEvent(preEvent);
        e.setCancelled(preEvent.isCancelled());
    }
}
