package com.kamikazejam.kamicommon.nms.abstraction.block;

import com.kamikazejam.kamicommon.nms.Logger;
import com.kamikazejam.kamicommon.util.data.MaterialData;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Slab;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class IBlockUtil1_13 extends IBlockUtil {
    public void set1_13BlockData(Block b, MaterialData materialData, boolean lightUpdate, boolean physics) {
        if (materialData.getData() == 0) { return; }

        KamiCommon.get().getLogger().info("set1_13BlockData: " + materialData.getData() + " on Block: " + b.toString() + " with lightUpdate: " + lightUpdate + " and physics: " + physics);

        try {
            Method getBlockData = b.getClass().getDeclaredMethod("getBlockData");
            Method setBlockData = b.getClass().getDeclaredMethod("setBlockData", BlockData.class);
            getBlockData.setAccessible(true);
            setBlockData.setAccessible(true);

            BlockData blockData = (BlockData) getBlockData.invoke(b);
            if (blockData instanceof Levelled) {
                Levelled levelled = (Levelled) blockData;
                levelled.setLevel(materialData.getData());

                setBlockData.invoke(b, levelled);
                KamiCommon.get().getLogger().info("Attempted to set level to: " + (int) materialData.getData());
            }else {
                throw new RuntimeException("BlockData: " + blockData.toString() + " is not Levelled, could not set custom MaterialData: " + materialData.getData());
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public @Nullable BlockData tryLeveled(BlockData blockData, MaterialData materialData) {
        if (blockData instanceof Levelled) {
            Levelled levelled = (Levelled) blockData;
            levelled.setLevel(materialData.getData());
            return levelled;
        }else if (materialData.getMaterial().name().toLowerCase().contains("slab")) {
            // Slab
            Slab.Type slabType = Slab.Type.DOUBLE;
            if (materialData.getData() >= 8 && materialData.getData() <= 15) { slabType = Slab.Type.TOP; }
            if (materialData.getData() >= 0 && materialData.getData() <= 7) { slabType = Slab.Type.BOTTOM; }

            Slab slab = (Slab) blockData;
            slab.setType(slabType);
            return slab;
        }else {
            Logger.warning("BlockData is not Levelled: " + blockData.getClass().getName());
        }
        return null;
    }
}
