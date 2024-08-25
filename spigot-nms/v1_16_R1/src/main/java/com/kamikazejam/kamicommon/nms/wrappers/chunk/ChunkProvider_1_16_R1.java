package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld_1_16_R1;
import net.minecraft.server.v1_16_R1.ChunkProviderServer;
import org.bukkit.craftbukkit.v1_16_R1.CraftChunk;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ChunkProvider_1_16_R1 implements NMSChunkProvider {
    private final @NotNull NMSWorld_1_16_R1 world;
    private final @NotNull ChunkProviderServer handle;
    public ChunkProvider_1_16_R1(@NotNull NMSWorld_1_16_R1 world, @NotNull ChunkProviderServer server) {
        this.world = world;
        this.handle = server;
    }

    @Override
    public @NotNull NMSWorld getNMSWorld() {
        return this.world;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.handle;
    }

    @Override
    public @NotNull NMSChunk getOrCreateChunk(int x, int z) {
        return new Chunk_1_16_R1(this, Objects.requireNonNull(this.handle.getChunkAt(x, z, true)));
    }

    @Override
    public void saveChunk(@NotNull NMSChunk chunk) {
    }

    @Override
    public @NotNull NMSChunk wrap(org.bukkit.@NotNull Chunk chunk) {
        return new Chunk_1_16_R1(this, ((CraftChunk) chunk).getHandle());
    }
}
