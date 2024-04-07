package com.kamikazejam.kamicommon.nms.abstraction.block;

import com.kamikazejam.kamicommon.xseries.XMaterial;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

/**
 * For versions 1_13_R1 and above (once BlockState was introduced)
 * {@link X} is the IBlockData type for the corresponding version.
 */
@SuppressWarnings("unused")
public abstract class IBlockUtil1_13<X> extends AbstractBlockUtil {

    // ---------------------------------------------------------------------------------------- //
    //                                     ABSTRACTION                                          //
    // ---------------------------------------------------------------------------------------- //

    /**
     * Get the IBlockData from the given BlockData (via nms).
     */
    public abstract X getIBlockData(@NotNull BlockData blockData);
    /**
     * The physics=false, light=false nms method to set a block.
     */
    public abstract void setNMS(@NotNull Block b, @NotNull X ibd);



    // ---------------------------------------------------------------------------------------- //
    //                                     API METHODS                                          //
    // ---------------------------------------------------------------------------------------- //
    /**
     * Set a block with the following parameters.
     * @param location The {@link Location} of the block to set
     * @param blockData The {@link BlockData} to use
     * @param placeType The {@link PlaceType} to use
     */
    public final void setBlockSuperFast(@NotNull Location location, @NotNull BlockData blockData, @NotNull PlaceType placeType) {
        this.setBlockSuperFast(location.getBlock(), blockData, placeType);
    }

    /**
     * Set a block with the following parameters.
     * @param block The {@link Block} to set
     * @param blockData The {@link BlockData} to use
     * @param placeType The {@link PlaceType} to use
     */
    public final void setBlockSuperFast(@NotNull Block block, @NotNull BlockData blockData, @NotNull PlaceType placeType) {
        if (placeType == PlaceType.BUKKIT) {
            // physics = true, light = true
            block.setBlockData(blockData, true);
        }else if (placeType == PlaceType.NO_PHYSICS) {
            // physics = false, light = true
            block.setBlockData(blockData, false);

        }else if (placeType == PlaceType.NMS) {
            // physics = false, light = false
            this.setNMS(block, this.getIBlockData(blockData));
        }
    }

    @Override
    public final void setBlock(@NotNull Block block, @NotNull XMaterial xMaterial, @NotNull PlaceType placeType) {
        // In 1.13 the flattening occurred, so now we can disregard the data value in XMaterial
        assert xMaterial.parseMaterial() != null;

        if (placeType == PlaceType.BUKKIT) {
            // physics = true, light = true
            block.setType(xMaterial.parseMaterial(), true);
        }else if (placeType == PlaceType.NO_PHYSICS) {
            // physics = false, light = true
            block.setType(xMaterial.parseMaterial(), false);

        }else if (placeType == PlaceType.NMS) {
            // physics = false, light = false
            this.setNMS(block, getIBlockData(createBlockData(xMaterial)));
        }
    }



    // ---------------------------------------------------------------------------------------- //
    //                                    UTIL METHODS                                          //
    // ---------------------------------------------------------------------------------------- //
    public final BlockData createBlockData(@NotNull XMaterial xMaterial) {
        // In 1.13 the flattening occurred, so now we can disregard the data value in XMaterial
        assert xMaterial.parseMaterial() != null;
        return xMaterial.parseMaterial().createBlockData();
    }
}

