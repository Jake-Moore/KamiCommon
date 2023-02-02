package com.kamikazejamplugins.kamicommon.nms.block;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.Chunk;
import net.minecraft.server.v1_16_R3.IBlockData;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

public class BlockUtil1_16_R3 extends IBlockUtil {

    @Override
    public void setCombined(Block b, int combined, boolean lightUpdate, boolean physics) {
        WorldServer w = ((CraftWorld) b.getWorld()).getHandle();
        Chunk chunk = w.getChunkAt(b.getX() >> 4, b.getZ() >> 4);
        BlockPosition bp = new BlockPosition(b.getX(), b.getY(), b.getZ());
        IBlockData ibd = net.minecraft.server.v1_16_R3.Block.getByCombinedId(combined);

        if (lightUpdate) {
            w.setTypeAndData(bp, ibd, (physics) ? 3 : 2); // applyPhysics = 3, 2 is none
        }else {
            try {
                chunk.setType(bp, ibd, physics, false);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            w.notify(bp, ibd, ibd, 0);
        }
    }

}
