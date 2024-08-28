package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import com.kamikazejam.kamicommon.nms.wrappers.chunk.impl.NMSChunkDef;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacket;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Chunk_1_17_R1 implements NMSChunkDef {
    private final @NotNull ChunkProvider_1_17_R1 provider;
    private final @NotNull Chunk bukkitChunk;
    private final @NotNull LevelChunk chunk;
    public Chunk_1_17_R1(@NotNull ChunkProvider_1_17_R1 provider, @NotNull Chunk bukkitChunk) {
        this.provider = provider;
        this.bukkitChunk = bukkitChunk;
        this.chunk = ((CraftChunk) bukkitChunk).getHandle();
    }

    @Override
    public @NotNull NMSChunkProvider getNMSChunkProvider() {
        return this.provider;
    }

    @Override
    public @NotNull org.bukkit.Chunk getBukkitChunk() {
        return bukkitChunk;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.chunk;
    }

    @Override
    public @NotNull NMSChunkSection getSection(final int y) {
        return new ChunkSection_1_17_R1(this, this.chunk.getSections()[y]);
    }

    @Override
    public @NotNull NMSChunkSection getOrCreateSection(final int y) {
        if (this.chunk.getSections()[y] == null) {
            LevelChunkSection chunkSection = new LevelChunkSection(y << 4, this.chunk, this.chunk.level, true);
            this.chunk.getSections()[y] = chunkSection;
        }
        return new ChunkSection_1_17_R1(this, this.chunk.getSections()[y]);
    }

    @Override
    public void clearTileEntities() {
        this.chunk.blockEntities.clear();
    }

    @Override
    public void sendUpdatePacket(@NotNull Player player) {
        ClientboundLevelChunkPacket packet = new ClientboundLevelChunkPacket(this.chunk, true);
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    @Override
    public int getX() {
        return this.chunk.getPos().x;
    }

    @Override
    public int getZ() {
        return this.chunk.getPos().z;
    }
}
