package com.kamikazejamplugins.kamicommon.nms.block;

import com.kamikazejamplugins.kamicommon.KamiCommon;
import com.kamikazejamplugins.kamicommon.util.VectorW;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class BlockUtil1_8_R3 extends IBlockUtil {
    @Override
    public void setCombined(VectorW v, int combined, boolean lightUpdate, boolean physics) {

        WorldServer w = ((CraftWorld) v.getWorld()).getHandle();
        Chunk chunk = w.getChunkAt(v.getBlockX() >> 4, v.getBlockZ() >> 4);
        BlockPosition bp = new BlockPosition(v.getBlockX(), v.getBlockY(), v.getBlockZ());
        //chunk.getSections()[bp.getY() >> 4].setType(); //TODO investigate

        IBlockData ibd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(combined);

        if (lightUpdate) {
            w.setTypeAndData(bp, ibd, (physics) ? 3 : 2); // applyPhysics = 3, 2 is none
        }else {
            try {
                if (KamiCommon.isWineSpigot()) {
                    chunk.a(bp, ibd, false, true);
                }else {
                    chunk.a(bp, ibd);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            w.notify(bp);
        }
    }
}
