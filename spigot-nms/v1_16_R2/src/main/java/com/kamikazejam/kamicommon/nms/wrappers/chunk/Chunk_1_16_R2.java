package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import net.minecraft.server.v1_16_R2.Chunk;
import net.minecraft.server.v1_16_R2.ChunkSection;
import net.minecraft.server.v1_16_R2.PacketPlayOutMapChunk;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Chunk_1_16_R2 implements NMSChunk {
    private final @NotNull ChunkProvider_1_16_R2 provider;
    private final @NotNull Chunk chunk;
    public Chunk_1_16_R2(@NotNull ChunkProvider_1_16_R2 provider, @NotNull Chunk chunk) {
        this.provider = provider;
        this.chunk = chunk;
    }

    @Override
    public @NotNull NMSChunkProvider getNMSChunkProvider() {
        return this.provider;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.chunk;
    }

    @Override
    public @NotNull NMSChunkSection getSection(final int y) {
        return new ChunkSection_1_16_R2(this, this.chunk.getSections()[y]);
    }

    @Override
    public @NotNull NMSChunkSection getOrCreateSection(final int y) {
        if (this.chunk.getSections()[y] == null) {
            ChunkSection chunkSection = new ChunkSection(y << 4);
            this.chunk.getSections()[y] = chunkSection;
        }
        return new ChunkSection_1_16_R2(this, this.chunk.getSections()[y]);
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
