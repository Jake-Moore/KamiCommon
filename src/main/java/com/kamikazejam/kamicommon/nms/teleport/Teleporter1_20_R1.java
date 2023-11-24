package com.kamikazejam.kamicommon.nms.teleport;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.lang.reflect.Method;

@SuppressWarnings({"deprecation", "DuplicatedCode"})

public class Teleporter1_20_R1 extends ITeleporter {

    @Override
    public void teleportWithoutEvent(Player player, Location location) {
        if (player.getVehicle() != null) {
            player.getVehicle().eject();
        }
        if (location.getWorld() == null) { return; }
        final WorldServer toWorld = ((CraftWorld)location.getWorld()).getHandle();
        final WorldServer fromWorld = ((CraftWorld)player.getWorld()).getHandle();
        final EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        if (toWorld == fromWorld) {
            try {
                PlayerConnection p = (PlayerConnection) entityPlayer.getClass().getDeclaredField("c").get(entityPlayer);
                p.teleport(location);
            }catch (Exception e) {
                e.printStackTrace();
            }

            // Verified for 1.20.1
            // entityPlayer.c.teleport(location);
        }
        else {
            try {
                PlayerList playerList = MinecraftServer.getServer().ac();
                Method method = playerList.getClass().getDeclaredMethod("respawn", EntityPlayer.class, WorldServer.class, boolean.class, Location.class, boolean.class, PlayerRespawnEvent.RespawnReason.class);
                method.setAccessible(true);
                method.invoke(playerList, entityPlayer, toWorld, true, location, true, PlayerRespawnEvent.RespawnReason.PLUGIN);
            }catch (Exception e) {
                e.printStackTrace();
            }

            // Verified for 1.20.1
            // MinecraftServer.getServer().ac().respawn(entityPlayer, toWorld, true, location, true, PlayerRespawnEvent.RespawnReason.PLUGIN);
        }
    }
}
