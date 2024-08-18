package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import net.minecraft.server.v1_15_R1.ChunkProviderServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftChunk;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ChunkProvider_1_15_R1 implements NMSChunkProvider {
    private final @NotNull ChunkProviderServer handle;
    public ChunkProvider_1_15_R1(@NotNull ChunkProviderServer server) {
        this.handle = server;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.handle;
    }

    @Override
    public @NotNull NMSChunk getOrCreateChunk(int x, int z) {
        return new Chunk_1_15_R1(Objects.requireNonNull(this.handle.getChunkAt(x, z, true)));
    }

    @Override
    public void saveChunk(@NotNull NMSChunk chunk) {
    }

    @Override
    public @NotNull NMSChunk wrap(org.bukkit.@NotNull Chunk chunk) {
        return new Chunk_1_15_R1(((CraftChunk) chunk).getHandle());
    }
}
