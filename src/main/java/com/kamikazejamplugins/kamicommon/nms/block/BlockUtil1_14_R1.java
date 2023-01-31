package com.kamikazejamplugins.kamicommon.nms.block;

import com.kamikazejamplugins.kamicommon.util.VectorW;
import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.Chunk;
import net.minecraft.server.v1_14_R1.IBlockData;
import net.minecraft.server.v1_14_R1.WorldServer;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;

public class BlockUtil1_14_R1 extends IBlockUtil {

    @Override
    public void setCombined(VectorW v, int combined, boolean lightUpdate, boolean physics) {

        WorldServer w = ((CraftWorld) v.getWorld()).getHandle();
        Chunk chunk = w.getChunkAt(v.getBlockX() >> 4, v.getBlockZ() >> 4);
        BlockPosition bp = new BlockPosition(v.getBlockX(), v.getBlockY(), v.getBlockZ());

        IBlockData ibd = net.minecraft.server.v1_14_R1.Block.getByCombinedId(combined);

        if (lightUpdate) {
            w.setTypeAndData(bp, ibd, (physics) ? 3 : 2); // applyPhysics = 3, 2 is none
        }else {
            try {
                // I think
                chunk.a(bp, combined, physics);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            w.notify(bp, ibd, ibd, 0);
        }
    }
}
