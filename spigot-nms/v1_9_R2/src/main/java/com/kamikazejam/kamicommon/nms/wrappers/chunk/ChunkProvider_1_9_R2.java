package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import net.minecraft.server.v1_9_R2.Chunk;
import net.minecraft.server.v1_9_R2.ChunkProviderServer;
import org.bukkit.craftbukkit.v1_9_R2.CraftChunk;
import org.jetbrains.annotations.NotNull;

public class ChunkProvider_1_9_R2 implements NMSChunkProvider {
    private final @NotNull ChunkProviderServer handle;
    public ChunkProvider_1_9_R2(@NotNull ChunkProviderServer server) {
        this.handle = server;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.handle;
    }

    @Override
    public @NotNull NMSChunk getOrCreateChunk(int x, int z) {
        return new Chunk_1_9_R2(this.handle.getChunkAt(x, z));
    }

    @Override
    public void saveChunk(NMSChunk chunk) {
        this.handle.saveChunk((Chunk) chunk.getHandle());
    }

    @Override
    public @NotNull NMSChunk wrap(org.bukkit.@NotNull Chunk chunk) {
        return new Chunk_1_9_R2(((CraftChunk) chunk).getHandle());
    }
}
