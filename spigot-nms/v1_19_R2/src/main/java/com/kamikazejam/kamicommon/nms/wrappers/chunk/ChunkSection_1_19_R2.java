package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R2.util.CraftMagicNumbers;
import org.jetbrains.annotations.NotNull;

public class ChunkSection_1_19_R2 implements NMSChunkSection {
    private final @NotNull LevelChunk chunk;
    private final int yShift;
    public ChunkSection_1_19_R2(@NotNull LevelChunk chunk, int yShift) {
        this.chunk = chunk;
        this.yShift = yShift;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.chunk;
    }

    @Override
    public void setType(int x, int y, int z, Material material) {
        y += (this.yShift << 4);
        this.chunk.setBlockState(new BlockPos(x, y, z), CraftMagicNumbers.getBlock(material).defaultBlockState(), false, false);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
