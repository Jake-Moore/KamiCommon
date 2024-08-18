package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.NotNull;

public class Chunk_1_20_R3 implements NMSChunk {
    private final @NotNull ChunkAccess chunk;
    public Chunk_1_20_R3(@NotNull ChunkAccess chunk) {
        this.chunk = chunk;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.chunk;
    }

    @Override
    public @NotNull NMSChunkSection getSection(int y) {
        return new ChunkSection_1_20_R3(this.chunk, y);
    }

    @Override
    public @NotNull NMSChunkSection getOrCreateSection(int y) {
        return this.getSection(y);
    }

    @Override
    public void clearTileEntities() {
        this.chunk.blockEntities.clear();
    }
}
