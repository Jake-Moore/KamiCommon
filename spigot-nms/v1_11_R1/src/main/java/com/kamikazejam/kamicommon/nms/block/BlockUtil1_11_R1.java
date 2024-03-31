package com.kamikazejam.kamicommon.nms.block;

import com.kamikazejam.kamicommon.nms.abstraction.block.IBlockUtilPre1_13;
import com.kamikazejam.kamicommon.util.data.MaterialData;
import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.Chunk;
import net.minecraft.server.v1_11_R1.IBlockData;
import net.minecraft.server.v1_11_R1.WorldServer;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;

@SuppressWarnings({"DuplicatedCode", "deprecation"})
public class BlockUtil1_11_R1 extends IBlockUtilPre1_13 {
    @Override
    public void setBlockInternal(Block b, MaterialData data, boolean lightUpdate, boolean physics) {
        // !!! We take physics as priority over light updates (physics == true --> light = true)

        // 1. physics = true (light = ?)
        if (physics) {
            // Unfortunately we cannot handle light=false with physics=true
            b.setTypeIdAndData(data.getMaterial().getId(), data.getData(), true);
            return;
        }

        // 2. physics = false && light = true
        if (lightUpdate) {
            // and we want light
            b.setTypeIdAndData(data.getMaterial().getId(), data.getData(), false);
            return;
        }

        // 3. physics = false && light = false
        WorldServer w = ((CraftWorld) b.getWorld()).getHandle();
        Chunk chunk = w.getChunkAt(b.getX() >> 4, b.getZ() >> 4);
        BlockPosition bp = new BlockPosition(b.getX(), b.getY(), b.getZ());

        IBlockData old = net.minecraft.server.v1_11_R1.Block.getByCombinedId(getCombined(b.getType().getId(), b.getData()));
        IBlockData ibd = net.minecraft.server.v1_11_R1.Block.getByCombinedId(getCombined(data));
        try {
            chunk.a(bp, ibd);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        w.notify(bp, old, ibd, 0);
    }
}
