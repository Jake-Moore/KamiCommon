package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld_1_21_CB;
import net.minecraft.server.level.ServerChunkCache;
import org.jetbrains.annotations.NotNull;

public class ChunkProvider_1_21_CB implements NMSChunkProvider {
    private final @NotNull NMSWorld_1_21_CB world;
    private final @NotNull ServerChunkCache handle;
    public ChunkProvider_1_21_CB(@NotNull NMSWorld_1_21_CB world, @NotNull ServerChunkCache server) {
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
    public void saveChunk(@NotNull NMSChunk chunk) {
    }

    @Override
    public @NotNull NMSChunk wrap(org.bukkit.@NotNull Chunk chunk) {
        return new Chunk_1_21_CB(this, chunk);
    }
}
