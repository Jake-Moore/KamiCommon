package com.kamikazejam.kamicommon.nms.abstraction.block;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

/**
 * For versions 1_13_R1 and above (until BlockState was introduced)
 */
@SuppressWarnings("unused")
public abstract class IBlockUtil1_13 extends IBlockUtil {

    /**
     * Set a block with the given Material.
     * @param block The block to set
     * @param blockData The BlockData to use
     * @param placeType The PlaceType to use
     */
    public abstract void setBlockSuperFast(@NotNull Block block, @NotNull BlockData blockData, @NotNull PlaceType placeType);

    /**
     * Set a block with the given XMaterial.
     * @param location The location of the block to set
     * @param blockData The BlockData to use
     * @param placeType The PlaceType to use
     */
    public final void setBlockSuperFast(@NotNull Location location, @NotNull BlockData blockData, @NotNull PlaceType placeType) {
        this.setBlockSuperFast(location.getBlock(), blockData, placeType);
    }
}

