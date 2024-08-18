package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import net.minecraft.world.level.chunk.LevelChunkSection;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftMagicNumbers;
import org.jetbrains.annotations.NotNull;

public class ChunkSection_1_17_R1 implements NMSChunkSection {
    private final @NotNull LevelChunkSection section;
    public ChunkSection_1_17_R1(@NotNull LevelChunkSection section) {
        this.section = section;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.section;
    }

    @Override
    public void setType(int x, int y, int z, Material material) {
        this.section.setBlockState(x, y, z, CraftMagicNumbers.getBlock(material).defaultBlockState());
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
