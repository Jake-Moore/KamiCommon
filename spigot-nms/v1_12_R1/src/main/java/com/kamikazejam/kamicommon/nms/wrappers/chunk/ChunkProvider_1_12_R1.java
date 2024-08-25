package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld_1_12_R1;
import net.minecraft.server.v1_12_R1.Chunk;
import net.minecraft.server.v1_12_R1.ChunkProviderServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftChunk;
import org.jetbrains.annotations.NotNull;

public class ChunkProvider_1_12_R1 implements NMSChunkProvider {
    private final @NotNull NMSWorld_1_12_R1 world;
    private final @NotNull ChunkProviderServer handle;
    public ChunkProvider_1_12_R1(@NotNull NMSWorld_1_12_R1 world, @NotNull ChunkProviderServer server) {
        this.world = world;
        this.handle = server;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.handle;
    }

    @Override
    public @NotNull NMSWorld getNMSWorld() {
        return this.world;
    }

    @Override
    public @NotNull NMSChunk getOrCreateChunk(int x, int z) {
        return new Chunk_1_12_R1(this, this.handle.getChunkAt(x, z));
    }

    @Override
    public void saveChunk(NMSChunk chunk) {
        this.handle.saveChunk((Chunk) chunk.getHandle(), false);
    }

    @Override
    public @NotNull NMSChunk wrap(org.bukkit.@NotNull Chunk chunk) {
        return new Chunk_1_12_R1(this, ((CraftChunk) chunk).getHandle());
    }
}
