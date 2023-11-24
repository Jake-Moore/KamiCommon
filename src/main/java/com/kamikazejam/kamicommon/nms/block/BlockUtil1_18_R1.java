package com.kamikazejam.kamicommon.nms.block;

import com.kamikazejam.kamicommon.util.MaterialData;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_18_R1.block.CraftBlock;

import javax.annotation.Nullable;

@SuppressWarnings("DuplicatedCode")
public class BlockUtil1_18_R1 extends IBlockUtil1_13 {

    @Override
    boolean supportsCombined() {
        return false;
    }

    @Override
    void setMaterialData(Block b, MaterialData materialData, boolean lightUpdate, boolean physics) {
        CraftBlock craftBlock = (CraftBlock) b;
        craftBlock.setType(materialData.getMaterial(), physics);

        if (materialData.getData() == 0) { return; }
        @Nullable BlockData blockData = tryLeveled(craftBlock.getBlockData(), materialData);
        if (blockData != null) { craftBlock.setBlockData(blockData, physics); }
    }
}
