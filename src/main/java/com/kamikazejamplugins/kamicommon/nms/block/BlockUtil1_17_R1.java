package com.kamikazejamplugins.kamicommon.nms.block;

import com.kamikazejamplugins.kamicommon.util.MaterialData;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock;

import javax.annotation.Nullable;

public class BlockUtil1_17_R1 extends IBlockUtil {

    @Override
    public boolean supportsCombined() { return false; }

    @Override
    void setMaterialData(Block b, MaterialData materialData, boolean lightUpdate, boolean physics) {
        CraftBlock craftBlock = (CraftBlock) b;
        craftBlock.setType(materialData.getMaterial(), physics);

        if (materialData.getData() == 0) { return; }
        @Nullable BlockData blockData = tryLeveled(craftBlock.getBlockData(), materialData);
        if (blockData != null) { craftBlock.setBlockData(blockData, physics); }
    }
}
