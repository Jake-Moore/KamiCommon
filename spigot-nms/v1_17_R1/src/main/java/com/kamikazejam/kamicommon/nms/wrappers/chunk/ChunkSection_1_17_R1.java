package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import net.minecraft.world.level.chunk.LevelChunkSection;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftMagicNumbers;
import org.jetbrains.annotations.NotNull;

public class ChunkSection_1_17_R1 implements NMSChunkSection_1_13 {
    private final @NotNull Chunk_1_17_R1 chunk;
    private final @NotNull LevelChunkSection section;
    public ChunkSection_1_17_R1(@NotNull Chunk_1_17_R1 chunk, @NotNull LevelChunkSection section) {
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
        this.section.setBlockState(x, y, z, CraftMagicNumbers.getBlock(material).defaultBlockState());
    }

    @Override
    public void setType(int x, int y, int z, @NotNull BlockData blockData) {
        this.section.setBlockState(x, y, z, ((CraftBlockData) blockData).getState());
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
