package com.kamikazejam.kamicommon.nms.teleport;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Objects;

@SuppressWarnings({"deprecation", "DuplicatedCode"})

public class Teleporter1_17_R1 extends ITeleporter {

    @Override
    public void teleportWithoutEvent(Player player, Location location) {
        if (player.getVehicle() != null) {
            player.getVehicle().eject();
        }
        final WorldServer toWorld = ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle();
        final WorldServer fromWorld = ((CraftWorld)player.getWorld()).getHandle();
        final EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        if (toWorld == fromWorld) {
            entityPlayer.b.teleport(location);
        }
        else {
            // Verified for 1.17 R1
            // MinecraftServer.getServer().getPlayerList().moveToWorld(entityPlayer, toWorld, true, location, true);

            try {
                Method getPlayerList = MinecraftServer.getServer().getClass().getDeclaredMethod("getPlayerList");
                getPlayerList.setAccessible(true);
                Object playerList = getPlayerList.invoke(MinecraftServer.getServer());
                Method moveToWorld = playerList.getClass().getDeclaredMethod("moveToWorld", EntityPlayer.class, WorldServer.class, boolean.class, Location.class, boolean.class);
                moveToWorld.setAccessible(true);
                moveToWorld.invoke(playerList, entityPlayer, toWorld, true, location, true);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
