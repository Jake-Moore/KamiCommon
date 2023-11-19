package com.kamikazejam.kamicommon.nms.block;

import net.minecraft.server.v1_13_R1.BlockPosition;
import net.minecraft.server.v1_13_R1.Chunk;
import net.minecraft.server.v1_13_R1.IBlockData;
import net.minecraft.server.v1_13_R1.WorldServer;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;

public class BlockUtil1_13_R1 extends IBlockUtil {

    @Override
    public void setCombined(Block b, int combined, boolean lightUpdate, boolean physics) {
        WorldServer w = ((CraftWorld) b.getWorld()).getHandle();
        Chunk chunk = w.getChunkAt(b.getX() >> 4, b.getZ() >> 4);
        BlockPosition bp = new BlockPosition(b.getX(), b.getY(), b.getZ());
        IBlockData ibd = net.minecraft.server.v1_13_R1.Block.getByCombinedId(combined);
        if (lightUpdate) {
            w.setTypeAndData(bp, ibd, (physics) ? 3 : 2); // applyPhysics = 3, 2 is none
        }else {
            try {
                chunk.a(bp, ibd, physics);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            w.notify(bp, ibd, ibd, 0);
        }
    }

}
