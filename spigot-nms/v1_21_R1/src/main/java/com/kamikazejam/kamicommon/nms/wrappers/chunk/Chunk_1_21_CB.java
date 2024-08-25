package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Chunk_1_21_CB implements NMSChunk {
    private final @NotNull ChunkProvider_1_21_CB provider;
    private final @NotNull ChunkAccess chunk;
    public Chunk_1_21_CB(@NotNull ChunkProvider_1_21_CB provider, @NotNull ChunkAccess chunk) {
        this.provider = provider;
        this.chunk = chunk;
    }

    @Override
    public @NotNull NMSChunkProvider getNMSChunkProvider() {
        return this.provider;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.chunk;
    }

    @Override
    public @NotNull NMSChunkSection getSection(int y) {
        return new ChunkSection_1_21_CB(this, this.chunk, y);
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
        if (this.chunk instanceof LevelChunk levelChunk) {
            Packet<?> packet = new ClientboundLevelChunkWithLightPacket(levelChunk, levelChunk.getLevel().getLightEngine(), null, null, true);
            ((CraftPlayer) player).getHandle().connection.send(packet);
        }else {
            throw new IllegalArgumentException("Chunk is not an instance of LevelChunk");
        }
    }
}
