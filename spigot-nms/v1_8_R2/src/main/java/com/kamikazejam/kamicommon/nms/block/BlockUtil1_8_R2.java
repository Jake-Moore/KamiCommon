package com.kamikazejam.kamicommon.nms.block;

import com.kamikazejam.kamicommon.nms.abstraction.block.IBlockUtilPre1_13;
import com.kamikazejam.kamicommon.util.data.MaterialData;
import net.minecraft.server.v1_8_R2.BlockPosition;
import net.minecraft.server.v1_8_R2.Chunk;
import net.minecraft.server.v1_8_R2.IBlockData;
import net.minecraft.server.v1_8_R2.WorldServer;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;

@SuppressWarnings("deprecation")
public class BlockUtil1_8_R2 extends IBlockUtilPre1_13 {
    @Override
    public void setBlockInternal(Block block, MaterialData data, boolean lightUpdate, boolean physics) {
        // !!! We take physics as priority over light updates (physics == true --> light = true)

        // 1. physics = true (light = ?)
        if (physics) {
            // Unfortunately we cannot handle light=false with physics=true in 1.8
            block.setTypeIdAndData(data.getMaterial().getId(), data.getData(), true);
            return;
        }

        // 2. physics = false && light = true
        if (lightUpdate) {
            // and we want light
            block.setTypeIdAndData(data.getMaterial().getId(), data.getData(), false);
            return;
        }

        // 3. physics = false && light = false
        WorldServer w = ((CraftWorld) block.getWorld()).getHandle();
        Chunk chunk = w.getChunkAt(block.getX() >> 4, block.getZ() >> 4);
        BlockPosition bp = new BlockPosition(block.getX(), block.getY(), block.getZ());

        IBlockData ibd = net.minecraft.server.v1_8_R2.Block.getByCombinedId(getCombined(data));
        try {
            chunk.a(bp, ibd);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        w.notify(bp);
    }
}
