package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import net.minecraft.server.v1_16_R2.ChunkSection;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_16_R2.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_16_R2.util.CraftMagicNumbers;
import org.jetbrains.annotations.NotNull;

public class ChunkSection_1_16_R2 implements NMSChunkSection_1_13 {
    private final @NotNull Chunk_1_16_R2 chunk;
    private final @NotNull ChunkSection section;
    public ChunkSection_1_16_R2(@NotNull Chunk_1_16_R2 chunk, @NotNull ChunkSection section) {
        this.chunk = chunk;
        this.section = section;
    }

    @Override
    public @NotNull NMSChunk getNMSChunk() {
        return this.chunk;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.section;
    }

    @Override
    public void setType(int x, int y, int z, @NotNull Material material) {
        this.section.setType(x, y, z, CraftMagicNumbers.getBlock(material).getBlockData());
    }

    @Override
    public void setType(int x, int y, int z, @NotNull BlockData blockData) {
        this.section.setType(x, y, z, ((CraftBlockData) blockData).getState());
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
