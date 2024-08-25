package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftMagicNumbers;
import org.jetbrains.annotations.NotNull;

public class ChunkSection_1_19_R1 implements NMSChunkSection_1_13 {
    private final @NotNull Chunk_1_19_R1 nmsChunk;
    private final @NotNull LevelChunk chunk;
    private final int yShift;
    public ChunkSection_1_19_R1(@NotNull Chunk_1_19_R1 nmsChunk, @NotNull LevelChunk chunk, int yShift) {
        this.nmsChunk = nmsChunk;
        this.chunk = chunk;
        this.yShift = yShift;
    }

    @Override
    public @NotNull NMSChunk getNMSChunk() {
        return this.nmsChunk;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.chunk;
    }

    @Override
    public void setType(int x, int y, int z, @NotNull Material material) {
        y += (this.yShift << 4);
        this.chunk.setBlockState(new BlockPos(x, y, z), CraftMagicNumbers.getBlock(material).defaultBlockState(), false, false);
    }

    @Override
    public void setType(int x, int y, int z, @NotNull BlockData blockData) {
        y += (this.yShift << 4);
        this.chunk.setBlockState(new BlockPos(x, y, z), ((CraftBlockData) blockData).getState(), false, false);
    }

    @Override
    public boolean isEmpty() {
        return this.chunk.getSection(this.chunk.getSectionIndexFromSectionY(yShift)).hasOnlyAir();
    }
}
