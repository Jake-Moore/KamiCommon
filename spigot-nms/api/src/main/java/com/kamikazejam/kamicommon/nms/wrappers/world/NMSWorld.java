package com.kamikazejam.kamicommon.nms.wrappers.world;

import com.kamikazejam.kamicommon.nms.abstraction.block.AbstractBlockUtil;
import com.kamikazejam.kamicommon.nms.wrappers.NMSObject;
import com.kamikazejam.kamicommon.nms.wrappers.chunk.NMSChunkProvider;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface NMSWorld extends NMSObject {
    @NotNull
    World getBukkitWorld();

    @NotNull
    NMSChunkProvider getChunkProvider();
    int getMinHeight();
    int getMaxHeight();
    void refreshBlockAt(@NotNull Player player, int x, int y, int z);

    @NotNull
    <T extends org.bukkit.entity.Entity> T spawnEntity(@NotNull Location loc, @NotNull Class<T> clazz, @NotNull CreatureSpawnEvent.SpawnReason reason);

    // Internal API method
    @NotNull
    AbstractBlockUtil getBlockUtil();
}
