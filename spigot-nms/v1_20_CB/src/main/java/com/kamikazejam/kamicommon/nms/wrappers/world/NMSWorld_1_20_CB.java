package com.kamikazejam.kamicommon.nms.wrappers.world;

import com.kamikazejam.kamicommon.nms.wrappers.chunk.ChunkProvider_1_20_CB;
import com.kamikazejam.kamicommon.nms.wrappers.chunk.NMSChunkProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NMSWorld_1_20_CB implements NMSWorld {
    private final @NotNull ServerLevel serverLevel;
    public NMSWorld_1_20_CB(@NotNull World world) {
        this.serverLevel = ((CraftWorld) world).getHandle();
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
        return new ChunkProvider_1_20_CB(this.serverLevel.getChunkSource());
    }

    @Override
    public void refreshBlockAt(@NotNull Player player, int x, int y, int z) {
        BlockPos blockPosition = new BlockPos(x, y, z);
        ClientboundBlockUpdatePacket change = new ClientboundBlockUpdatePacket(this.serverLevel, blockPosition);
        ((CraftPlayer) player).getHandle().connection.send(change);
    }
}
