package com.kamikazejam.kamicommon.nms.abstraction.block;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.KamiCommon;
import com.kamikazejam.kamicommon.nms.NmsVersion;
import com.kamikazejam.kamicommon.util.data.MaterialData;
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
        if (NmsVersion.getFormattedNmsDouble() < 1130) {
            throw new UnsupportedOperationException("Didn't override .setMaterialData in BlockUtil");
        }

        KamiCommon.get().getLogger().info("Setting MaterialData: " + materialData.toString() + " on Block: " + b.toString() + " with lightUpdate: " + lightUpdate + " and physics: " + physics);

        b.setType(materialData.getMaterial());
        set1_13BlockData(b, materialData, lightUpdate, physics);
    }

    boolean supportsCombined() {
        return true;
    }
}
