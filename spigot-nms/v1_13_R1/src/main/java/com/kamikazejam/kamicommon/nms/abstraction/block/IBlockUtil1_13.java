package com.kamikazejam.kamicommon.nms.abstraction.block;

import com.kamikazejam.kamicommon.util.data.XBlockData;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Slab;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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
    public final void setBlock(@NotNull Block b, @NotNull XBlockData blockData, @NotNull PlaceType placeType) {
        // In 1.13 the flattening occurred, so now we can disregard the data value in XMaterial
        XMaterial xMaterial = blockData.getMaterialData().getMaterial();
        assert xMaterial.parseMaterial() != null;

        // Create a BlockData object, which may get set if we have additional BlockData properties
        @Nullable BlockData data = findBlockData(b.getLocation().toVector(), blockData);
        if (data != null) {
            // If we have data from a custom property, set using that instead
            this.setBlockSuperFast(b, data, placeType);
            return;
        }

        if (placeType == PlaceType.BUKKIT) {
            // physics = true, light = true
            b.setType(xMaterial.parseMaterial(), true);
        }else if (placeType == PlaceType.NO_PHYSICS) {
            // physics = false, light = true
            b.setType(xMaterial.parseMaterial(), false);

        }else if (placeType == PlaceType.NMS) {
            // physics = false, light = false
            this.setNMS(b, getIBlockData(createBlockData(xMaterial)));
        }
    }



    // ---------------------------------------------------------------------------------------- //
    //                                    UTIL METHODS                                          //
    // ---------------------------------------------------------------------------------------- //
    public static @NotNull BlockData getOrCreateBlockData(@Nullable BlockData data, @NotNull XMaterial xMaterial) {
        return (data == null) ? createBlockData(xMaterial) : data;
    }
    public static BlockData createBlockData(@NotNull XMaterial xMaterial) {
        // In 1.13 the flattening occurred, so now we can disregard the data value in XMaterial
        assert xMaterial.parseMaterial() != null;
        return xMaterial.parseMaterial().createBlockData();
    }

    public static @Nullable BlockData findBlockData(@NotNull Vector v, @NotNull XBlockData xData) {
        @NotNull XMaterial xMaterial = Objects.requireNonNull(xData.getMaterialData().getMaterial());
        @Nullable BlockData blockData = null;

        // Apply Levelled block data
        if (xData.getLevel() != null) {
            // We have a level, so make a BlockData with this value
            blockData = getOrCreateBlockData(blockData, xMaterial);
            if (blockData instanceof Levelled levelled) {
                levelled.setLevel(xData.getLevel());
            }else {
                throw new IllegalArgumentException("[KamiCommon] [IBlockUtil] tried setting block at "
                        + "(" + v.getBlockX() + "," + v.getBlockY() + "," + v.getBlockZ() + ")"
                        + " with type: " + xMaterial.name()
                        + " and level: " + xData.getLevel()
                        + " but the BlockData is not a Levelled block,"
                        + " actual: " + blockData.getClass().getSimpleName());
            }
        }

        // Apply slab type
        if (xData.getSlabType() != null) {
            Slab.Type slabType = Slab.Type.valueOf(xData.getSlabType().name());
            blockData = getOrCreateBlockData(blockData, xMaterial);
            if (blockData instanceof Slab slab) {
                slab.setType(slabType);
            }else {
                throw new IllegalArgumentException("[KamiCommon] [IBlockUtil] tried setting block at "
                        + "(" + v.getBlockX() + "," + v.getBlockY() + "," + v.getBlockZ() + ")"
                        + " with type: " + xMaterial.name()
                        + " and slab type: " + xData.getSlabType()
                        + " but the BlockData is not a Slab block,"
                        + " actual: " + blockData.getClass().getSimpleName());
            }
        }


        return blockData;
    }
}

