package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import com.kamikazejam.kamicommon.nms.abstraction.block.IBlockUtil1_13;
import com.kamikazejam.kamicommon.util.data.XBlockData;
import com.kamikazejam.kamicommon.xseries.XMaterial;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public interface NMSChunkSection_1_13 extends NMSChunkSection {
    void setType(int x, int y, int z, @NotNull BlockData blockData);

    @Override
    default void setType(int x, int y, int z, @NotNull XBlockData xBlockData) {
        // In 1.13 the flattening occurred, so now we can disregard the data value in XMaterial
        XMaterial xMaterial = xBlockData.getMaterialData().getMaterial();
        assert xMaterial.parseMaterial() != null;

        // Create a BlockData object, which may get set if we have additional BlockData properties
        @Nullable BlockData data = IBlockUtil1_13.findBlockData(new Vector(x, y, z), xBlockData, xMaterial);
        if (data != null) {
            // If we have data from a custom property, set using that instead
            this.setType(x, y, z, data);
            return;
        }

        // Use the default block data
        BlockData defData = IBlockUtil1_13.createBlockData(xMaterial);
        this.setType(x, y, z, defData);
    }
}
