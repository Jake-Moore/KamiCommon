package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;

public class Chunk_1_19_R2 implements NMSChunk {
    private final @NotNull LevelChunk chunk;
    public Chunk_1_19_R2(@NotNull LevelChunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.chunk;
    }

    @Override
    public @NotNull NMSChunkSection getSection(int y) {
        return new ChunkSection_1_19_R2(this.chunk, y);
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
