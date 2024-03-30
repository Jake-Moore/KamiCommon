package com.kamikazejam.kamicommon.nms.block;

import com.kamikazejam.kamicommon.nms.NmsVersion;
import com.kamikazejam.kamicommon.nms.abstraction.block.IBlockUtilPre1_13;
import com.kamikazejam.kamicommon.util.data.MaterialData;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

@SuppressWarnings({"deprecation", "DuplicatedCode"})
public class BlockUtil1_8_R3 extends IBlockUtilPre1_13 {
    @Override
    public void setBlockInternal(Block block, MaterialData data, boolean lightUpdate, boolean physics) {
        if (NmsVersion.isWineSpigot()) {
            handleWineSpigot(block, data, lightUpdate, physics);
        }else {
            handleSpigot(block, data, lightUpdate, physics);
        }
    }

    private void handleSpigot(Block block, MaterialData data, boolean lightUpdate, boolean physics) {
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

        IBlockData ibd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(getCombined(data));
        try {
            chunk.a(bp, ibd);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        w.notify(bp);
    }

    // WineSpigot adds a really handy chunk method that accepts both lightUpdate and noPlace booleans
    private void handleWineSpigot(Block b, MaterialData data, boolean lightUpdate, boolean physics) {
        // 1. Handle light == true  &&  physics == ?
        if (lightUpdate) {
            b.setTypeIdAndData(data.getMaterial().getId(), data.getData(), physics);
            return;
        }

        // 2. Handle light == false && physics == ?
        WorldServer w = ((CraftWorld) b.getWorld()).getHandle();
        Chunk chunk = w.getChunkAt(b.getX() >> 4, b.getZ() >> 4);
        BlockPosition bp = new BlockPosition(b.getX(), b.getY(), b.getZ());

        IBlockData ibd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(getCombined(data));
        chunk.a(bp, ibd, false, !physics); // TODO check if 'noPlace' is inverted from physics
    }
}
