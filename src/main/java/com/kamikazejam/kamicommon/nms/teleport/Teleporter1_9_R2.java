package com.kamikazejam.kamicommon.nms.teleport;

import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.MinecraftServer;
import net.minecraft.server.v1_9_R2.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Objects;

@SuppressWarnings({"deprecation", "DuplicatedCode"})
public class Teleporter1_9_R2 extends ITeleporter {

    @Override
    public void teleportWithoutEvent(Player player, Location location) {
        if (player.getVehicle() != null) {
            player.getVehicle().eject();
        }
        final WorldServer toWorld = ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle();
        final WorldServer fromWorld = ((CraftWorld)player.getWorld()).getHandle();
        final EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        if (toWorld == fromWorld) {
            entityPlayer.playerConnection.teleport(location);
        }
        else {
            MinecraftServer.getServer().getPlayerList().moveToWorld(entityPlayer, toWorld.dimension, true, location, true);
        }
    }
}
