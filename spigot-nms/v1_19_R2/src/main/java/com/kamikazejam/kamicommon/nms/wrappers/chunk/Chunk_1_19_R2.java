package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import com.kamikazejam.kamicommon.nms.wrappers.chunk.impl.NMSChunkDef;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_19_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Chunk_1_19_R2 implements NMSChunkDef {
    private final @NotNull ChunkProvider_1_19_R2 provider;
    private final @NotNull Chunk bukkitChunk;
    private final @NotNull LevelChunk chunk;
    public Chunk_1_19_R2(@NotNull ChunkProvider_1_19_R2 provider, @NotNull Chunk bukkitChunk) {
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
    public @NotNull NMSChunkSection getSection(int y) {
        return new ChunkSection_1_19_R2(this, this.chunk, y);
    }

    @Override
    public @NotNull NMSChunkSection getOrCreateSection(int y) {
        return this.getSection(y);
    }

    @Override
    public void clearTileEntities() {
        this.chunk.blockEntities.clear();
    }

    @Override
    public void sendUpdatePacket(@NotNull Player player) {
        Packet<?> packet = new ClientboundLevelChunkWithLightPacket(this.chunk, this.chunk.getLevel().getLightEngine(), null, null, true, true);
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
