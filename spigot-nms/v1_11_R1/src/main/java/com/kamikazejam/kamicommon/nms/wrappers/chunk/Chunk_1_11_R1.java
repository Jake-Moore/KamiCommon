package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import net.minecraft.server.v1_11_R1.Chunk;
import net.minecraft.server.v1_11_R1.ChunkSection;
import net.minecraft.server.v1_11_R1.PacketPlayOutMapChunk;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Chunk_1_11_R1 implements NMSChunk {
    private final @NotNull ChunkProvider_1_11_R1 provider;
    private final @NotNull Chunk chunk;
    public Chunk_1_11_R1(@NotNull ChunkProvider_1_11_R1 provider, @NotNull Chunk chunk) {
        this.provider = provider;
        this.chunk = chunk;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.chunk;
    }

    @Override
    public @NotNull NMSChunkProvider getNMSChunkProvider() {
        return this.provider;
    }

    @Override
    public @NotNull NMSChunkSection getSection(final int y) {
        return new ChunkSection_1_11_R1(this, this.chunk.getSections()[y]);
    }

    @Override
    public @NotNull NMSChunkSection getOrCreateSection(final int y) {
        if (this.chunk.getSections()[y] == null) {
            ChunkSection chunkSection = new ChunkSection(y << 4, !this.chunk.world.worldProvider.m());
            this.chunk.getSections()[y] = chunkSection;
        }
        return new ChunkSection_1_11_R1(this, this.chunk.getSections()[y]);
    }

    @Override
    public void clearTileEntities() {
        this.chunk.tileEntities.clear();
    }

    @Override
    public void sendUpdatePacket(@NotNull Player player) {
        PacketPlayOutMapChunk packet = new PacketPlayOutMapChunk(this.chunk, '\uffff');
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
