package com.kamikazejamplugins.kamicommon.nms.block;

import com.kamikazejamplugins.kamicommon.nms.NmsManager;
import com.kamikazejamplugins.kamicommon.util.MaterialData;
import com.kamikazejamplugins.kamicommon.util.VectorW;
import org.bukkit.block.Block;

public class BlockUtil1_9_to_1_16 extends IBlockUtil {

    @Override
    public boolean supportsCombined() {
        return false;
    }

    @SuppressWarnings({"deprecation"})
    @Override
    public void setMaterialData(VectorW v, MaterialData materialData, boolean lightUpdate, boolean physics) {

        Block b = v.toLocation().getBlock();

        // Start off by setting the material
        b.setType(materialData.getMaterial());

        // If we have a custom data value, we need to do more work
        if (materialData.getData() != 0) {
            // For 1.12 and lower, there are setData methods
            if (NmsManager.getFormattedNmsDouble() <= 1.12) {
                b.setData(materialData.getData(), physics);

            // For 1.13+ there are BlockData methods, but we need reflection because they are not in the API
            //  Can't have both in the API at the same time sadly, they share the same Block class

            // In reality, we should never get here, we shouldn't have a data value in 1.13+
            }else {
                set1_13BlockData(v, materialData, lightUpdate, physics);
            }
        }
    }
}
