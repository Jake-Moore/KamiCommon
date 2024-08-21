package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_20_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftMagicNumbers;
import org.jetbrains.annotations.NotNull;

public class ChunkSection_1_20_R1 implements NMSChunkSection_1_13 {
    private final @NotNull ChunkAccess chunk;
    private final int yShift;
    public ChunkSection_1_20_R1(@NotNull ChunkAccess chunk, int yShift) {
        this.chunk = chunk;
        this.yShift = yShift;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.chunk;
    }

    @Override
    public void setType(int x, int y, int z, @NotNull Material material) {
        y += (this.yShift << 4);
        if (this.chunk instanceof LevelChunk c) {
            c.setBlockState(new BlockPos(x, y, z), CraftMagicNumbers.getBlock(material).defaultBlockState(), false, false);
        }else {
            this.chunk.setBlockState(new BlockPos(x, y, z), CraftMagicNumbers.getBlock(material).defaultBlockState(), false);
        }
    }

    @Override
    public void setType(int x, int y, int z, @NotNull BlockData blockData) {
        BlockState state = ((CraftBlockData) blockData).getState();
        if (this.chunk instanceof LevelChunk c) {
            c.setBlockState(new BlockPos(x, y, z), state, false, false);
        }else {
            this.chunk.setBlockState(new BlockPos(x, y, z), state, false);
        }
    }

    @Override
    public boolean isEmpty() {
        return this.chunk.getSection(yShift).hasOnlyAir();
    }
}
