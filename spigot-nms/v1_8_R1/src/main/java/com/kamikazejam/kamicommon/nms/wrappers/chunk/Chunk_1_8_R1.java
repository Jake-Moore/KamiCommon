package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import com.kamikazejam.kamicommon.nms.wrappers.chunk.impl.NMSChunkDef;
import net.minecraft.server.v1_8_R1.Chunk;
import net.minecraft.server.v1_8_R1.ChunkSection;
import net.minecraft.server.v1_8_R1.PacketPlayOutMapChunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R1.util.LongHash;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Chunk_1_8_R1 implements NMSChunkDef {
    private final @NotNull ChunkProvider_1_8_R1 provider;
    private final @NotNull org.bukkit.Chunk bukkitChunk;
    private final @NotNull Chunk chunk;
    public Chunk_1_8_R1(@NotNull ChunkProvider_1_8_R1 provider, @NotNull org.bukkit.Chunk bukkitChunk) {
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
        return new ChunkSection_1_8_R1(this, this.chunk.getSections()[y]);
    }

    @Override
    public @NotNull NMSChunkSection getOrCreateSection(final int y) {
        if (this.chunk.getSections()[y] == null) {
            ChunkSection chunkSection = new ChunkSection(y << 4, !this.chunk.world.worldProvider.o());
            this.chunk.getSections()[y] = chunkSection;
        }
        return new ChunkSection_1_8_R1(this, this.chunk.getSections()[y]);
    }

    @Override
    public void clearTileEntities() {
        this.chunk.tileEntities.clear();
    }

    @Override
    public void sendUpdatePacket(@NotNull Player player) {
        PacketPlayOutMapChunk packet = new PacketPlayOutMapChunk(this.chunk, true, '\uffff');
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public int getX() {
        return this.chunk.locX;
    }

    @Override
    public int getZ() {
        return this.chunk.locZ;
    }

    @Override
    public void saveAndRefresh(boolean withUpdatePackets) {
        // Do regular save and refresh
        this.saveAndRefreshI(withUpdatePackets);

        // Re-cache the chunk, which fixes a race condition if the chunk was currently in use
        World world = this.getNMSChunkProvider().getNMSWorld().getBukkitWorld();
        ((CraftWorld) world).getHandle().chunkProviderServer.chunks.put(LongHash.toLong(chunk.locX, chunk.locZ), chunk);
    }
}
