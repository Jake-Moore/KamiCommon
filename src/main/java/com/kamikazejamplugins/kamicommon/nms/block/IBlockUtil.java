package com.kamikazejamplugins.kamicommon.nms.block;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejamplugins.kamicommon.KamiCommon;
import com.kamikazejamplugins.kamicommon.nms.NmsManager;
import com.kamikazejamplugins.kamicommon.util.MaterialData;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings({"deprecation", "unused"})
public abstract class IBlockUtil {
    public void setBlockSuperFast(Block b, XMaterial xMaterial, boolean lightUpdate, boolean physics) {
        if (supportsCombined()) {
            setCombined(b, xMaterial.getId() + (xMaterial.getData() << 12), lightUpdate, physics);
        }else {
            assert xMaterial.parseMaterial() != null;
            setMaterialData(b, new MaterialData(xMaterial.parseMaterial(), xMaterial.getData()), lightUpdate, physics);
        }
    }

    public void setBlockSuperFast(Block b, Material material, boolean lightUpdate, boolean physics) {
        if (supportsCombined()) {
            setCombined(b, material.getId(), lightUpdate, physics);
        }else {
            setMaterialData(b, new MaterialData(material, (byte) 0), lightUpdate, physics);
        }
    }

    public void setBlockSuperFast(Block b, MaterialData materialData, boolean lightUpdate, boolean physics) {
        if (supportsCombined()) {
            setCombined(b, materialData.getMaterial().getId() + (materialData.getData() << 12), lightUpdate, physics);
        }else {
            setMaterialData(b, materialData, lightUpdate, physics);
        }
    }

    void setCombined(Block b, int combined, boolean lightUpdate, boolean physics) {
        throw new UnsupportedOperationException("Didn't override .setCombined in BlockUtil");
    }

    void setMaterialData(Block b, MaterialData materialData, boolean lightUpdate, boolean physics) {
        if (NmsManager.getFormattedNmsDouble() < 1.13) {
            throw new UnsupportedOperationException("Didn't override .setMaterialData in BlockUtil");
        }

        KamiCommon.get().getLogger().info("Setting MaterialData: " + materialData.toString() + " on Block: " + b.toString() + " with lightUpdate: " + lightUpdate + " and physics: " + physics);

        b.setType(materialData.getMaterial());
        set1_13BlockData(b, materialData, lightUpdate, physics);
    }

    boolean supportsCombined() {
        return true;
    }

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
}
