package com.kamikazejam.kamicommon.block;

import com.kamikazejam.kamicommon.nms.abstraction.block.IBlockUtil1_13;
import com.kamikazejam.kamicommon.util.data.MaterialData;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("DuplicatedCode")
public class BlockUtil1_17_R1 extends IBlockUtil1_13 {

    @Override
    public void setBlockInternal(Block block, MaterialData data, boolean lightUpdate, boolean physics) {
        CraftWorld world = (CraftWorld) block.getWorld();
        ServerLevel serverLevel = world.getHandle();
        serverLevel.setblock
    }

    @Override
    public @Nullable BlockData tryLeveled(BlockData blockData, MaterialData materialData) {

    }

    @Override
    public boolean supportsCombined() { return false; }

    @Override
    void setMaterialData(Block b, MaterialData materialData, boolean lightUpdate, boolean physics) {
        CraftBlock craftBlock = (CraftBlock) b;
        craftBlock.setType(materialData.getMaterial(), physics);
        craftBlock.settype



        if (materialData.getData() == 0) { return; }
        @Nullable BlockData blockData = tryLeveled(craftBlock.getBlockData(), materialData);
        if (blockData != null) { craftBlock.setBlockData(blockData, physics); }
    }
}
