package com.kamikazejam.kamicommon.nms.wrappers.world;

import com.kamikazejam.kamicommon.nms.wrappers.chunk.ChunkProvider_1_9_R1;
import com.kamikazejam.kamicommon.nms.wrappers.chunk.NMSChunkProvider;
import lombok.Getter;
import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.PacketPlayOutBlockChange;
import net.minecraft.server.v1_9_R1.WorldServer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

public class NMSWorld_1_9_R1 implements NMSWorld {
    @Getter
    private final @NotNull World bukkitWorld;
    private final @NotNull WorldServer worldServer;
    private final @NotNull CraftWorld craftWorld;
    public NMSWorld_1_9_R1(@NotNull World world) {
        this.bukkitWorld = world;
        this.craftWorld = (CraftWorld) world;
        this.worldServer = this.craftWorld.getHandle();
    }

    @Override
    public @NotNull Object getHandle() {
        return this.worldServer;
    }

    @Override
    public int getMinHeight() {
        return 0;
    }

    @Override
    public int getMaxHeight() {
        return this.bukkitWorld.getMaxHeight();
    }

    @Override
    public @NotNull NMSChunkProvider getChunkProvider() {
        return new ChunkProvider_1_9_R1(this, this.worldServer.getChunkProviderServer());
    }

    @Override
    public void refreshBlockAt(@NotNull Player player, int x, int y, int z) {
        BlockPosition blockPosition = new BlockPosition(x, y, z);
        PacketPlayOutBlockChange change = new PacketPlayOutBlockChange(this.worldServer, blockPosition);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(change);
    }

    @Override
    public <T extends Entity> @NotNull T spawnEntity(@NotNull Location location, @NotNull Class<T> aClass, CreatureSpawnEvent.@NotNull SpawnReason spawnReason) {
        return this.craftWorld.spawn(location, aClass, spawnReason);
    }
}
