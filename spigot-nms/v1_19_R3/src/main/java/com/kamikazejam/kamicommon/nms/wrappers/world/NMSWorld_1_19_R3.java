package com.kamikazejam.kamicommon.nms.wrappers.world;

import com.kamikazejam.kamicommon.nms.wrappers.chunk.ChunkProvider_1_19_R3;
import com.kamikazejam.kamicommon.nms.wrappers.chunk.NMSChunkProvider;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

public class NMSWorld_1_19_R3 implements NMSWorld {
    @Getter
    private final @NotNull World bukkitWorld;
    private final @NotNull ServerLevel serverLevel;
    private final @NotNull CraftWorld craftWorld;
    public NMSWorld_1_19_R3(@NotNull World world) {
        this.bukkitWorld = world;
        this.craftWorld = (CraftWorld) world;
        this.serverLevel = this.craftWorld.getHandle();
    }

    @Override
    public @NotNull Object getHandle() {
        return this.serverLevel;
    }

    @Override
    public int getMinHeight() {
        return this.serverLevel.getMinBuildHeight();
    }

    @Override
    public int getMaxHeight() {
        return this.serverLevel.getHeight();
    }

    @Override
    public @NotNull NMSChunkProvider getChunkProvider() {
        return new ChunkProvider_1_19_R3(this, this.serverLevel.getChunkSource());
    }

    @Override
    public void refreshBlockAt(@NotNull Player player, int x, int y, int z) {
        BlockPos blockPosition = new BlockPos(x, y, z);
        ClientboundBlockUpdatePacket change = new ClientboundBlockUpdatePacket(this.serverLevel, blockPosition);
        ((CraftPlayer) player).getHandle().connection.send(change);
    }

    @Override
    public <T extends Entity> @NotNull T spawnEntity(@NotNull Location location, @NotNull Class<T> aClass, CreatureSpawnEvent.@NotNull SpawnReason spawnReason) {
        return this.craftWorld.spawn(location, aClass, (e) -> {}, spawnReason);
    }
}
