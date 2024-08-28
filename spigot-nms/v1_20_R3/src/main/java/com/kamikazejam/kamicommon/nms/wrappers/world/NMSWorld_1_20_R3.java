package com.kamikazejam.kamicommon.nms.wrappers.world;

import com.kamikazejam.kamicommon.nms.abstraction.block.AbstractBlockUtil;
import com.kamikazejam.kamicommon.nms.provider.Provider;
import com.kamikazejam.kamicommon.nms.wrappers.chunk.ChunkProvider_1_20_R3;
import com.kamikazejam.kamicommon.nms.wrappers.chunk.NMSChunkProvider;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

public class NMSWorld_1_20_R3 implements NMSWorld {
    @Getter
    private final @NotNull World bukkitWorld;
    private final @NotNull ServerLevel serverLevel;
    private final @NotNull CraftWorld craftWorld;
    private final @NotNull Provider<AbstractBlockUtil> provider;
    public NMSWorld_1_20_R3(@NotNull World world, @NotNull Provider<AbstractBlockUtil> provider) {
        this.bukkitWorld = world;
        this.craftWorld = (CraftWorld) world;
        this.serverLevel = this.craftWorld.getHandle();
        this.provider = provider;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.serverLevel;
    }

    @Override
    public @NotNull AbstractBlockUtil getBlockUtil() {
        return this.provider.get();
    }

    @Override
    public int getMinHeight() {
        return this.bukkitWorld.getMinHeight();
    }

    @Override
    public int getMaxHeight() {
        return this.bukkitWorld.getMaxHeight();
    }

    @Override
    public @NotNull NMSChunkProvider getChunkProvider() {
        return new ChunkProvider_1_20_R3(this, this.serverLevel.getChunkSource());
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
