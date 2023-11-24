package com.kamikazejam.kamicommon.nms.teleport;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Objects;

@SuppressWarnings({"deprecation", "DuplicatedCode"})

public class Teleporter1_18_R1 extends ITeleporter {

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
            // Verified for 1.18 R1
            MinecraftServer.getServer().ac().respawn(entityPlayer, toWorld, true, location, true);
        }
    }
}
