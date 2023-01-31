package com.kamikazejamplugins.kamicommon.nms.block;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejamplugins.kamicommon.util.MaterialData;
import com.kamikazejamplugins.kamicommon.util.VectorW;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings({"deprecation", "unused"})
public abstract class IBlockUtil {
    public void setBlockSuperFast(VectorW v, XMaterial xMaterial, boolean lightUpdate, boolean physics) {
        if (supportsCombined()) {
            setCombined(v, xMaterial.getId() + (xMaterial.getData() << 12), lightUpdate, physics);
        }else {
            assert xMaterial.parseMaterial() != null;
            setMaterialData(v, new MaterialData(xMaterial.parseMaterial(), xMaterial.getData()), lightUpdate, physics);
        }
    }

    public void setBlockSuperFast(VectorW v, Material material, boolean lightUpdate, boolean physics) {
        if (supportsCombined()) {
            setCombined(v, material.getId(), lightUpdate, physics);
        }else {
            setMaterialData(v, new MaterialData(material, (byte) 0), lightUpdate, physics);
        }
    }

    public void setBlockSuperFast(VectorW v, MaterialData materialData, boolean lightUpdate, boolean physics) {
        if (supportsCombined()) {
            setCombined(v, materialData.getMaterial().getId() + (materialData.getData() << 12), lightUpdate, physics);
        }else {
            setMaterialData(v, materialData, lightUpdate, physics);
        }
    }

    void setCombined(VectorW v, int combined, boolean lightUpdate, boolean physics) {
        throw new UnsupportedOperationException("Didn't override .setCombined in BlockUtil");
    }

    void setMaterialData(VectorW v, MaterialData materialData, boolean lightUpdate, boolean physics) {
        throw new UnsupportedOperationException("Didn't override .setMaterialData in BlockUtil");
    }

    boolean supportsCombined() {
        return true;
    }

    public void set1_13BlockData(VectorW v, MaterialData materialData, boolean lightUpdate, boolean physics) {

        Block b = v.toLocation().getBlock();
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
            }else {
                throw new RuntimeException("BlockData: " + blockData.toString() + " is not Levelled, could not set custom MaterialData: " + materialData.getData());
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
