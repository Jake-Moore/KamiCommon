package com.kamikazejamplugins.kamicommon.nms.block;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.block.Block;

public interface IBlockUtil {
    void setBlockSuperFast(Block b, XMaterial xMaterial, boolean lightUpdate, boolean physics);
}
