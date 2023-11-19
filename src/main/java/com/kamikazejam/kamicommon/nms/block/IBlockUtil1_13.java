package com.kamikazejam.kamicommon.nms.block;

import com.kamikazejam.kamicommon.KamiCommon;
import com.kamikazejam.kamicommon.util.MaterialData;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Slab;

import javax.annotation.Nullable;

public class IBlockUtil1_13 extends IBlockUtil {
    public @Nullable BlockData tryLeveled(BlockData blockData, MaterialData materialData) {
        if (blockData instanceof Levelled) {
            Levelled levelled = (Levelled) blockData;
            levelled.setLevel(materialData.getData());
            return levelled;
        }else if (materialData.getMaterial().name().toLowerCase().contains("slab")) {
            // Slab
            Slab.Type slabType = Slab.Type.DOUBLE;
            if (materialData.getData() >= 8 && materialData.getData() <= 15) { slabType = Slab.Type.TOP; }
            if (materialData.getData() >= 0 && materialData.getData() <= 7) { slabType = Slab.Type.BOTTOM; }

            Slab slab = (Slab) blockData;
            slab.setType(slabType);
            return slab;
        }else {
            KamiCommon.get().getLogger().warning("BlockData is not Levelled: " + blockData.getClass().getName());
        }
        return null;
    }
}
