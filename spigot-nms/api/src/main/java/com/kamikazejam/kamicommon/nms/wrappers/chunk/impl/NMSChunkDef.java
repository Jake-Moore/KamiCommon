package com.kamikazejam.kamicommon.nms.wrappers.chunk.impl;

import com.kamikazejam.kamicommon.nms.wrappers.chunk.NMSChunk;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

public interface NMSChunkDef extends NMSChunk {

    @Override
    default void saveAndRefresh(boolean withUpdatePackets) {
        this.saveAndRefreshI(withUpdatePackets);
    }

    // Create a default inside this internal interface, to not expose this method
    @SuppressWarnings("deprecation")
    default void saveAndRefreshI(boolean withUpdatePackets) {
        World world = this.getNMSChunkProvider().getNMSWorld().getBukkitWorld();
        world.refreshChunk(this.getX(), this.getZ());
        // Use the unload method with save
        world.unloadChunk(this.getX(), this.getZ(), true);

        // Re-send some chunk packets so that stubborn clients update
        if (withUpdatePackets) {
            int viewDistance = Bukkit.getViewDistance();
            for (Player player : world.getPlayers()) {
                int cX = player.getLocation().getChunk().getX();
                int cZ = player.getLocation().getChunk().getZ();

                if (this.getX() > cX + viewDistance || this.getX() < cX - viewDistance) { continue; }
                if (this.getZ() > cZ + viewDistance || this.getZ() < cZ - viewDistance) { continue; }

                this.sendUpdatePacket(player);
            }
        }
    }
}
