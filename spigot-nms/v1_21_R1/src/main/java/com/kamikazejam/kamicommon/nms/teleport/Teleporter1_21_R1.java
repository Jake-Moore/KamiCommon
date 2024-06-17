package com.kamikazejam.kamicommon.nms.teleport;

import com.kamikazejam.kamicommon.nms.abstraction.teleport.AbstractTeleporter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;

@SuppressWarnings({"DuplicatedCode"})
public class Teleporter1_21_R1 extends AbstractTeleporter {

    @Override
    public void teleportWithoutEvent(Player player, Location location) {
        if (player.getVehicle() != null) {
            player.getVehicle().eject();
        }
        if (location.getWorld() == null) { return; }

        final ServerLevel toWorld = ((CraftWorld)location.getWorld()).getHandle();
        final ServerLevel fromWorld = ((CraftWorld)player.getWorld()).getHandle();
        final ServerPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        if (toWorld == fromWorld) {
            entityPlayer.connection.teleport(location);
        }
        else {
            MinecraftServer.getServer().getPlayerList().respawn(
                    entityPlayer, true, Entity.RemovalReason.DISCARDED,
                    PlayerRespawnEvent.RespawnReason.PLUGIN, location
            );
        }
    }
}
