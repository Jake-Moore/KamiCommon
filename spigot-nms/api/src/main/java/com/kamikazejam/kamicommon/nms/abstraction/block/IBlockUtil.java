package com.kamikazejam.kamicommon.nms.abstraction.block;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.util.data.MaterialData;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * DO NOT EXTEND THIS CLASS. Use a sub class like {@link IBlockUtilPre1_13} instead.
 */
@SuppressWarnings("unused")
public abstract class IBlockUtil {
    private static final boolean IS_FLAT = XMaterial.supports(13);
    // ---------------------------------------------------------------------------------------- //
    //                                     ABSTRACTION                                          //
    // ---------------------------------------------------------------------------------------- //
    public abstract void setBlockInternal(Block block, MaterialData data, boolean lightUpdate, boolean physics);
    public abstract boolean youShouldNotBeSeeingThisIfYouExtendThisClass();



    // ---------------------------------------------------------------------------------------- //
    //                                     API METHODS                                          //
    // ---------------------------------------------------------------------------------------- //
    /**
     * Set a block with the given XMaterial.
     * @param block The block to set
     * @param xMaterial The XMaterial to use
     * @param lightUpdate If physics is true, this may be forced true as well
     * @param physics If the block should update the physics
     */
    public final void setBlockSuperFast(Block block, XMaterial xMaterial, boolean lightUpdate, boolean physics) {
        byte b = (IS_FLAT) ? (byte) 0 : xMaterial.getData();
        MaterialData materialData = new MaterialData(xMaterial.parseMaterial(), b);
        this.setBlockInternal(block, materialData, lightUpdate, physics);
    }
    /**
     * Set a block with the given XMaterial.
     * @param block The block to set
     * @param material The Material to use
     * @param lightUpdate If physics is true, this may be forced true as well
     * @param physics If the block should update the physics
     */
    public final void setBlockSuperFast(Block block, Material material, boolean lightUpdate, boolean physics) {
        this.setBlockInternal(block, new MaterialData(material, (byte) 0), lightUpdate, physics);
    }
    /**
     * Set a block with the given XMaterial.
     * @param block The block to set
     * @param materialData The MaterialData to use (KamiCommon Wrapper)
     * @param lightUpdate If physics is true, this may be forced true as well
     * @param physics If the block should update the physics
     */
    public final void setBlockSuperFast(Block block, MaterialData materialData, boolean lightUpdate, boolean physics) {
        this.setBlockInternal(block, materialData, lightUpdate, physics);
    }
}
