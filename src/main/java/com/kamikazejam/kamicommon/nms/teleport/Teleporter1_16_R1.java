package com.kamikazejam.kamicommon.nms.teleport;

import net.minecraft.server.v1_16_R1.EntityPlayer;
import net.minecraft.server.v1_16_R1.MinecraftServer;
import net.minecraft.server.v1_16_R1.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class Teleporter1_16_R1 extends ITeleporter {

    @Override
    public void teleportWithoutEvent(Player player, Location location) {
        if (player.getVehicle() != null) {
            player.getVehicle().eject();
        }
        final WorldServer toWorld = ((CraftWorld)location.getWorld()).getHandle();
        final WorldServer fromWorld = ((CraftWorld)player.getWorld()).getHandle();
        final EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        if (toWorld == fromWorld) {
            entityPlayer.playerConnection.teleport(location);
        }
        else {
            MinecraftServer.getServer().getPlayerList().moveToWorld(entityPlayer, toWorld, true, location, true);
        }
    }
}
