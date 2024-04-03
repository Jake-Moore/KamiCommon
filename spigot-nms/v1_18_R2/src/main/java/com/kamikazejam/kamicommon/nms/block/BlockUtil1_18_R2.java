package com.kamikazejam.kamicommon.nms.block;

import com.kamikazejam.kamicommon.nms.abstraction.block.IBlockUtil1_13;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.block.data.CraftBlockData;
import org.jetbrains.annotations.NotNull;

public class BlockUtil1_18_R2 extends IBlockUtil1_13<BlockState> {
    @Override
    public BlockState getIBlockData(@NotNull BlockData blockData) {
        return ((CraftBlockData) blockData).getState();
    }

    // physics = false, light = false
    @Override
    public void setNMS(@NotNull Block b, @NotNull BlockState ibd) {
        ServerLevel w = ((CraftWorld) b.getWorld()).getHandle();
        LevelChunk chunk = w.getChunk(b.getX() >> 4, b.getZ() >> 4);
        BlockPos bp = new BlockPos(b.getX(), b.getY(), b.getZ());

        BlockState old = chunk.getBlockStateIfLoaded(bp); // should be fine (and faster) to use this method
        try {
            // doPlace = false -> don't simulate player block place (should be faster)
            chunk.setBlockState(bp, ibd, false, false);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        w.sendBlockUpdated(bp, (old == null) ? ibd : old, ibd, 3);
    }
}
