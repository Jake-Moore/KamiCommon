package com.kamikazejamplugins.kamicommon.nms.block;

import com.kamikazejamplugins.kamicommon.KamiCommon;
import com.kamikazejamplugins.kamicommon.util.MaterialData;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.craftbukkit.v1_18_R2.block.CraftBlock;

public class BlockUtil1_18_R2 extends IBlockUtil {

    @Override
    boolean supportsCombined() {
        return false;
    }

    @Override
    void setMaterialData(Block b, MaterialData materialData, boolean lightUpdate, boolean physics) {
        CraftBlock craftBlock = (CraftBlock) b;
        craftBlock.setType(materialData.getMaterial(), physics);

        if (materialData.getData() == 0) { return; }

        BlockData blockData = craftBlock.getBlockData();
        if (blockData instanceof Levelled) {
            Levelled levelled = (Levelled) blockData;
            levelled.setLevel(materialData.getData());
            craftBlock.setBlockData(levelled, physics);
        }else {
            KamiCommon.get().getLogger().warning("BlockData is not Levelled: " + blockData.getClass().getName());
        }
    }
}
