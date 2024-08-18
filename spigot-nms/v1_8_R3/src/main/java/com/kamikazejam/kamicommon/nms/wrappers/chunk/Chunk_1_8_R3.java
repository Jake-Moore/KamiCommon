package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.ChunkSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;

public class Chunk_1_8_R3 implements NMSChunk {
    private final @NotNull Chunk chunk;
    private @Nullable Constructor<ChunkSection> constructor; // Alternate ChunkSection constructor
    public Chunk_1_8_R3(@NotNull Chunk chunk) {
        this.chunk = chunk;
        try {
            this.constructor = ChunkSection.class.getConstructor(int.class, boolean.class, int.class, int.class);
        }catch (NoSuchMethodException | SecurityException e) {
            this.constructor = null;
        }
    }

    @Override
    public @NotNull Object getHandle() {
        return this.chunk;
    }

    @Override
    public @NotNull NMSChunkSection getSection(final int y) {
        return new ChunkSection_1_8_R3(this.chunk.getSections()[y]);
    }

    @SneakyThrows
    @Override
    public @NotNull NMSChunkSection getOrCreateSection(final int y) {
        if (this.chunk.getSections()[y] == null) {
            ChunkSection chunkSection;
            if (constructor != null) {
                chunkSection = constructor.newInstance(y << 4, !this.chunk.world.worldProvider.o(), this.chunk.locX, this.chunk.locZ);
            } else {
                chunkSection = new ChunkSection(y << 4, !this.chunk.world.worldProvider.o());
            }
            this.chunk.getSections()[y] = chunkSection;
        }
        return new ChunkSection_1_8_R3(this.chunk.getSections()[y]);
    }

    @Override
    public void clearTileEntities() {
        this.chunk.tileEntities.clear();
    }
}
