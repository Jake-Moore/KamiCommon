package com.kamikazejam.kamicommon.nms.teleport;

import com.kamikazejam.kamicommon.nms.abstraction.teleport.AbstractTeleporter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Objects;

@SuppressWarnings({"deprecation", "DuplicatedCode"})
public class Teleporter1_17_R1 extends AbstractTeleporter {

    @Override
    public void teleportWithoutEvent(Player player, Location location) {
        if (player.getVehicle() != null) {
            player.getVehicle().eject();
        }
        final ServerLevel toWorld = ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle();
        final ServerLevel fromWorld = ((CraftWorld) player.getWorld()).getHandle();
        final ServerPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        if (toWorld == fromWorld) {
            entityPlayer.connection.teleport(location);
        } else {
            MinecraftServer.getServer().getPlayerList().moveToWorld(entityPlayer, toWorld, true, location, true);
        }
    }
}
