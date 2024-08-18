package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import net.minecraft.server.v1_14_R1.Chunk;
import net.minecraft.server.v1_14_R1.ChunkSection;
import org.jetbrains.annotations.NotNull;

public class Chunk_1_14_R1 implements NMSChunk {
    private final @NotNull Chunk chunk;
    public Chunk_1_14_R1(@NotNull Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.chunk;
    }

    @Override
    public @NotNull NMSChunkSection getSection(final int y) {
        return new ChunkSection_1_14_R1(this.chunk.getSections()[y]);
    }

    @Override
    public @NotNull NMSChunkSection getOrCreateSection(final int y) {
        if (this.chunk.getSections()[y] == null) {
            ChunkSection chunkSection = new ChunkSection(y << 4);
            this.chunk.getSections()[y] = chunkSection;
        }
        return new ChunkSection_1_14_R1(this.chunk.getSections()[y]);
    }

    @Override
    public void clearTileEntities() {
        this.chunk.tileEntities.clear();
    }
}