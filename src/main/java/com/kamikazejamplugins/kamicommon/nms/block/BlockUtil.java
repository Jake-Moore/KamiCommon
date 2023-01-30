package com.kamikazejamplugins.kamicommon.nms.block;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejamplugins.kamicommon.nms.NmsManager;
import org.bukkit.block.Block;

@SuppressWarnings("unused")
public class BlockUtil {
    public static void setBlockSuperFast(Block b, XMaterial xMaterial, boolean lightUpdate, boolean physics) {
        NmsManager.getBlockUtil().setBlockSuperFast(b, xMaterial, lightUpdate, physics);
    }
}
