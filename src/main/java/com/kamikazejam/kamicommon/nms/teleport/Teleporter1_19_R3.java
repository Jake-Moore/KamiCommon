package com.kamikazejam.kamicommon.nms.teleport;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

@SuppressWarnings("deprecation")
public class Teleporter1_19_R3 extends ITeleporter {

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
            entityPlayer.b.teleport(location);
        }
        else {

            // Verified for 1.19 R3 spigot jar
            try {
                PlayerList playerList = MinecraftServer.getServer().ac();
                Method method = playerList.getClass().getDeclaredMethod("respawn", EntityPlayer.class, WorldServer.class, boolean.class, Location.class, boolean.class);
                method.setAccessible(true);
                method.invoke(playerList, entityPlayer, toWorld, true, location, true);
            }catch (Exception e) {
                e.printStackTrace();
            }

            // This is the working method for a 1.19.3 jar
            MinecraftServer.getServer().ac().respawn(entityPlayer, toWorld, true, location, true);
        }
    }
}
