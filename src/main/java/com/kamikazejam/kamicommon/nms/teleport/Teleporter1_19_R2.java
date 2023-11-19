package com.kamikazejam.kamicommon.nms.teleport;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

@SuppressWarnings("deprecation")
public class Teleporter1_19_R2 extends ITeleporter {

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
            // Verified for 1.19 R2
            // MinecraftServer.getServer().ab().respawn(entityPlayer, toWorld, true, location, true);

            try {
                Method ab = MinecraftServer.getServer().getClass().getDeclaredMethod("ab");
                ab.setAccessible(true);
                Object playerList = ab.invoke(MinecraftServer.getServer());
                Method respawn = playerList.getClass().getDeclaredMethod("respawn", EntityPlayer.class, WorldServer.class, boolean.class, Location.class, boolean.class);
                respawn.setAccessible(true);
                respawn.invoke(playerList, entityPlayer, toWorld, true, location, true);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
