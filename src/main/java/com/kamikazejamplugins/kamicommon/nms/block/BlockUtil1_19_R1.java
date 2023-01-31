package com.kamikazejamplugins.kamicommon.nms.block;

import com.kamikazejamplugins.kamicommon.KamiCommon;
import com.kamikazejamplugins.kamicommon.util.MaterialData;
import com.kamikazejamplugins.kamicommon.util.VectorW;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;

import java.lang.reflect.Method;

@SuppressWarnings("deprecation")
public class BlockUtil1_19_R1 extends IBlockUtil {
    @Override
    boolean supportsCombined() {
        return false;
    }

    @Override
    public void setMaterialData(VectorW v, MaterialData materialData, boolean lightUpdate, boolean physics) {

        WorldServer w = ((CraftWorld) v.getWorld()).getHandle();
        // I think
        Chunk chunk = w.k().a(v.getBlockX() >> 4, v.getBlockZ() >> 4, true);
        if (chunk == null) {
            KamiCommon.get().getLogger().severe("[BlockUtil] Chunk is null! " + v.getX() + ", " + v.getZ());
            org.bukkit.block.Block b = v.toLocation().getBlock();
            b.setType(materialData.getMaterial());

            KamiCommon.get().getLogger().warning("[BlockUtil] Couldn't set Block Data for: " + b.getState());
            try {
                Method getBlockData = b.getClass().getDeclaredMethod("getBlockData");
                BlockData blockData = (BlockData) getBlockData.invoke(b);
                KamiCommon.get().getLogger().warning("[BlockUtil] BlockData: " + blockData.getClass().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        int combined = materialData.getMaterial().getId() + (materialData.getData() << 12);
        BlockPosition bp = new BlockPosition(v.getBlockX(), v.getBlockY(), v.getBlockZ());
        IBlockData ibd = Block.a(combined);

        if (lightUpdate) {
            // Probably
            w.a(bp, ibd, (physics) ? 3 : 2);
        }else {
            try {

                // I think
                chunk.setBlockState(bp, ibd, physics, true);

            } catch (Throwable t) {
                t.printStackTrace();
            }

            w.a(bp, ibd, ibd, 0);
        }
    }
}
