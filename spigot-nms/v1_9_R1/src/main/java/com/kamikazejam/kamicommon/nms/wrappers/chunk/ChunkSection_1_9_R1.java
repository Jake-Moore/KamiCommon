package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import net.minecraft.server.v1_9_R1.ChunkSection;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R1.util.CraftMagicNumbers;
import org.jetbrains.annotations.NotNull;

public class ChunkSection_1_9_R1 implements NMSChunkSection {
    private final @NotNull ChunkSection section;
    public ChunkSection_1_9_R1(@NotNull ChunkSection section) {
        this.section = section;
    }

    @Override
    public @NotNull Object getHandle() {
        return this.section;
    }

    @Override
    public void setType(int x, int y, int z, Material material) {
        this.section.setType(x, y, z, CraftMagicNumbers.getBlock(material).getBlockData());
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
