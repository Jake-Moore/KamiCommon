package com.kamikazejamplugins.kamicommon.nms.block;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.block.Block;

public class BlockUtil1_9_to_1_16 implements IBlockUtil {
    @SuppressWarnings("deprecation")
    @Override
    public void setBlockSuperFast(Block b, XMaterial xMaterial, boolean lightUpdate, boolean physics) {

        b.setType(xMaterial.parseMaterial());
        if (xMaterial.getData() != 0) {
            b.setData(xMaterial.getData());
        }
    }
}
