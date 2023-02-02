package com.kamikazejamplugins.kamicommon.nms.block;

import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.Chunk;
import net.minecraft.server.v1_13_R2.IBlockData;
import net.minecraft.server.v1_13_R2.WorldServer;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;

public class BlockUtil1_13_R2 extends IBlockUtil {
    @Override
    public void setCombined(Block b, int combined, boolean lightUpdate, boolean physics) {
        WorldServer w = ((CraftWorld) b.getWorld()).getHandle();
        Chunk chunk = w.getChunkAt(b.getX() >> 4, b.getZ() >> 4);
        BlockPosition bp = new BlockPosition(b.getX(), b.getY(), b.getZ());
        IBlockData ibd = net.minecraft.server.v1_13_R2.Block.getByCombinedId(combined);

        if (lightUpdate) {
            w.setTypeAndData(bp, ibd, (physics) ? 3 : 2); // applyPhysics = 3, 2 is none
        }else {
            try {
                chunk.a(bp, combined, physics);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            w.notify(bp, ibd, ibd, 0);
        }
    }
}
