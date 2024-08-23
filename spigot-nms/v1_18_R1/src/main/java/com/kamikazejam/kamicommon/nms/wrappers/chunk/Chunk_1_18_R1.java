package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Chunk_1_18_R1 implements NMSChunk {
    private final @NotNull LevelChunk chunk;
    public Chunk_1_18_R1(@NotNull LevelChunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.chunk;
    }

    @Override
    public @NotNull NMSChunkSection getSection(int y) {
        return new ChunkSection_1_18_R1(this.chunk, y);
    }

    @Override
    public @NotNull NMSChunkSection getOrCreateSection(int y) {
        return this.getSection(y);
    }

    @Override
    public void clearTileEntities() {
        this.chunk.blockEntities.clear();
    }

    @Override
    public void sendUpdatePacket(@NotNull Player player) {
        Packet<?> packet = new ClientboundLevelChunkWithLightPacket(this.chunk, this.chunk.getLevel().getLightEngine(), null, null, true, true);
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }
}
