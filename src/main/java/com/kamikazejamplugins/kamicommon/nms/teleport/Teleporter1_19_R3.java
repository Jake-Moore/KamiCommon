package com.kamikazejamplugins.kamicommon.nms.teleport;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class Teleporter1_19_R3 extends ITeleporter {

    @Override
    public void teleportWithoutEvent(Player player, Location location) {
        if (player.getVehicle() != null) {
            player.getVehicle().eject();
        }
        final WorldServer toWorld = ((CraftWorld)location.getWorld()).getHandle();
        final WorldServer fromWorld = ((CraftWorld)player.getWorld()).getHandle();
        final EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        if (toWorld == fromWorld) {
            entityPlayer.b.teleport(location);
        }
        else {
            // Verified for 1.19 R3
            MinecraftServer.getServer().ac().respawn(entityPlayer, toWorld, true, location, true);
        }
    }
}
