package com.kamikazejam.kamicommon.nms.block;

import com.kamikazejam.kamicommon.nms.Logger;
import com.kamikazejam.kamicommon.nms.abstraction.block.IBlockUtilPre1_13;
import com.kamikazejam.kamicommon.util.data.MaterialData;
import net.minecraft.server.v1_13_R1.BlockPosition;
import net.minecraft.server.v1_13_R1.Chunk;
import net.minecraft.server.v1_13_R1.IBlockData;
import net.minecraft.server.v1_13_R1.WorldServer;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Slab;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.jetbrains.annotations.Nullable;

public class BlockUtil1_13_R1 extends IBlockUtilPre1_13 {
    @Override
    public void setBlockInternal(Block b, MaterialData data, boolean lightUpdate, boolean physics) {
        // !!! We take physics as priority over light updates (physics == true --> light = true)
        BlockData blockData = b.getBlockData();

        // 1. physics = true (light = ?)
        if (physics) {
            // Unfortunately we cannot handle light=false with physics=true
            @Nullable BlockData specialData = tryLeveled(blockData, data);
            if (specialData == null) {
                b.setType(data.getMaterial(), true);
            }else {
                b.setBlockData(specialData, true);
            }
            return;
        }

        // 2. physics = false && light = true
        if (lightUpdate) {
            // and we want light
            @Nullable BlockData specialData = tryLeveled(blockData, data);
            if (specialData == null) {
                b.setType(data.getMaterial(), false);
            }else {
                b.setBlockData(specialData, false);
            }
            return;
        }

        // 3. physics = false && light = false
        WorldServer w = ((CraftWorld) b.getWorld()).getHandle();
        Chunk chunk = w.getChunkAt(b.getX() >> 4, b.getZ() >> 4);
        BlockPosition bp = new BlockPosition(b.getX(), b.getY(), b.getZ());

        IBlockData old = net.minecraft.server.v1_13_R1.Block.getByCombinedId(getCombined(b.getType().getId(), b.getData()));
        IBlockData ibd = net.minecraft.server.v1_13_R1.Block.getByCombinedId(getCombined(data));

        try {
            chunk.a(bp, ibd, false); // physics = false
        } catch (Throwable t) {
            t.printStackTrace();
        }
        w.notify(bp, old, ibd, 0);


    }

    @Override
    public @Nullable BlockData tryLeveled(BlockData blockData, MaterialData data) {
        // Set type already sets a data of 0, so we can skip this
        if (data.getData() == 0) { return null; }

        if (blockData instanceof Levelled) {
            Levelled levelled = (Levelled) blockData;
            if (levelled.getLevel() == data.getData()) { return null; } // Skip setting the same value
            levelled.setLevel(data.getData());
            return levelled;
        }else if (data.getMaterial().name().toLowerCase().contains("slab")) {
            // Slab
            Slab.Type slabType = Slab.Type.DOUBLE;
            if (data.getData() >= 8 && data.getData() <= 15) { slabType = Slab.Type.TOP; }
            if (data.getData() >= 0 && data.getData() <= 7) { slabType = Slab.Type.BOTTOM; }

            Slab slab = (Slab) blockData;
            if (slab.getType().equals(slabType)) { return null; } // Skip setting the same value
            slab.setType(slabType);
            return slab;
        }else {
            // this isAssignableFrom check will return true if BlockData is itself BlockData, not a subclass
            if (!isBlockDataExact(blockData)) {
                // If here then BlockData is a subinterface of BlockData, not found above
                Logger.warning("[BlockUtil1_13_R1] BlockData unrecognized: "
                        + blockData.getClass().getName());
            }
        }
        return null;
    }
//
//
//    @Override
//    public void setCombined(Block b, int combined, boolean lightUpdate, boolean physics) {
//        BlockData blockData = b.getBlockData()
//
//        WorldServer w = ((CraftWorld) b.getWorld()).getHandle();
//        Chunk chunk = w.getChunkAt(b.getX() >> 4, b.getZ() >> 4);
//        BlockPosition bp = new BlockPosition(b.getX(), b.getY(), b.getZ());
//        IBlockData ibd = net.minecraft.server.v1_13_R1.Block.getByCombinedId(combined);
//        if (lightUpdate) {
//            w.setTypeAndData(bp, ibd, (physics) ? 3 : 2); // applyPhysics = 3, 2 is none
//        }else {
//            try {
//                chunk.a(bp, ibd, physics);
//            } catch (Throwable t) {
//                t.printStackTrace();
//            }
//            w.notify(bp, ibd, ibd, 0);
//        }
//    }

}
