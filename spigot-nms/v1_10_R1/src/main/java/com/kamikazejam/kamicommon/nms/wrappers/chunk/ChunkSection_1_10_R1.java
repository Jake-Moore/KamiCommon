package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import com.kamikazejam.kamicommon.util.data.XBlockData;
import com.kamikazejam.kamicommon.util.data.XMaterialData;
import net.minecraft.server.v1_10_R1.ChunkSection;
import net.minecraft.server.v1_10_R1.IBlockData;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.util.CraftMagicNumbers;
import org.jetbrains.annotations.NotNull;

public class ChunkSection_1_10_R1 implements NMSChunkSection {
    private final @NotNull Chunk_1_10_R1 chunk;
    private final @NotNull ChunkSection section;
    public ChunkSection_1_10_R1(@NotNull Chunk_1_10_R1 chunk, @NotNull ChunkSection section) {
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
    @SuppressWarnings("deprecation")
    public void setType(int x, int y, int z, @NotNull XBlockData xBlockData) {
        // For pre-1.13 we use data values
        XMaterialData materialData = xBlockData.getMaterialData();

        byte data = materialData.getData();
        Material material = materialData.getMaterial().parseMaterial();
        assert material != null;

        IBlockData blockData = CraftMagicNumbers.getBlock(material).fromLegacyData(data);
        this.section.setType(x, y, z, blockData);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
