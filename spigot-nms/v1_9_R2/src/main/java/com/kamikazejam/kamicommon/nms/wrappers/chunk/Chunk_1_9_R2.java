package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import net.minecraft.server.v1_9_R2.Chunk;
import net.minecraft.server.v1_9_R2.ChunkSection;
import org.jetbrains.annotations.NotNull;

public class Chunk_1_9_R2 implements NMSChunk {
    private final @NotNull Chunk chunk;
    public Chunk_1_9_R2(@NotNull Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.chunk;
    }

    @Override
    public @NotNull NMSChunkSection getSection(final int y) {
        return new ChunkSection_1_9_R2(this.chunk.getSections()[y]);
    }

    @Override
    public @NotNull NMSChunkSection getOrCreateSection(final int y) {
        if (this.chunk.getSections()[y] == null) {
            ChunkSection chunkSection = new ChunkSection(y << 4, !this.chunk.world.worldProvider.m());
            this.chunk.getSections()[y] = chunkSection;
        }
        return new ChunkSection_1_9_R2(this.chunk.getSections()[y]);
    }

    @Override
    public void clearTileEntities() {
        this.chunk.tileEntities.clear();
    }
}
