package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.bukkit.craftbukkit.CraftChunk;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ChunkProvider_1_20_CB implements NMSChunkProvider {
    private final @NotNull ServerChunkCache handle;
    public ChunkProvider_1_20_CB(@NotNull ServerChunkCache server) {
        this.handle = server;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.handle;
    }

    @Override
    public @NotNull NMSChunk getOrCreateChunk(int x, int z) {
        return new Chunk_1_20_CB(Objects.requireNonNull(this.handle.getChunk(x, z, true)));
    }

    @Override
    public void saveChunk(@NotNull NMSChunk chunk) {
    }

    @Override
    public @NotNull NMSChunk wrap(org.bukkit.@NotNull Chunk chunk) {
        return new Chunk_1_20_CB(((CraftChunk) chunk).getHandle(ChunkStatus.FULL));
    }
}
