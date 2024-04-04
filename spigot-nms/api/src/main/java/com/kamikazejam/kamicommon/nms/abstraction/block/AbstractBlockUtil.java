package com.kamikazejam.kamicommon.nms.abstraction.block;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.util.Preconditions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * Methods every BlockUtil implementation has to implement.
 * Subclasses may add additional version-dependent utility methods.
 */
@SuppressWarnings("unused")
public abstract class AbstractBlockUtil {
    // ---------------------------------------------------------------------------------------- //
    //                                     ABSTRACTION                                          //
    // ---------------------------------------------------------------------------------------- //
    public abstract void setBlock(@NotNull Block block, @NotNull XMaterial xMaterial, @NotNull PlaceType placeType);
    private void setBlockInternal(@NotNull Block block, @NotNull XMaterial xMaterial, @NotNull PlaceType placeType) {
        Preconditions.checkNotNull(block, "Block cannot be null");
        Preconditions.checkNotNull(xMaterial, "XMaterial cannot be null");
        Preconditions.checkNotNull(placeType, "PlaceType cannot be null");
        Preconditions.checkNotNull(xMaterial.parseMaterial(), "Material cannot be null");
        this.setBlock(block, xMaterial, placeType);
    }



    // ---------------------------------------------------------------------------------------- //
    //                                     API METHODS                                          //
    // ---------------------------------------------------------------------------------------- //
    /**
     * Set a block with the following parameters.
     * @param block The {@link Block} to set
     * @param xMaterial The {@link XMaterial} to use
     * @param placeType The {@link PlaceType} to use
     */
    public final void setBlockSuperFast(@NotNull Block block, @NotNull XMaterial xMaterial, @NotNull PlaceType placeType) {
        this.setBlockInternal(block, xMaterial, placeType);
    }
    /**
     * Set a block with the following parameters.
     * @param block The {@link Block} to set
     * @param material The {@link Material} to use
     * @param placeType The {@link PlaceType} to use
     */
    public final void setBlockSuperFast(@NotNull Block block, @NotNull Material material, @NotNull PlaceType placeType) {
        this.setBlockInternal(block, XMaterial.matchXMaterial(material), placeType);
    }

    /**
     * Set a block with the following parameters.
     * @param location The {@link Location} of the block to set
     * @param xMaterial The {@link XMaterial} to use
     * @param placeType The {@link PlaceType} to use
     */
    public final void setBlockSuperFast(@NotNull Location location, @NotNull XMaterial xMaterial, @NotNull PlaceType placeType) {
        this.setBlockInternal(location.getBlock(), xMaterial, placeType);
    }
    /**
     * Set a block with the following parameters.
     * @param location The {@link Location} of the block to set
     * @param material The {@link Material} to use
     * @param placeType The {@link PlaceType} to use
     */
    public final void setBlockSuperFast(@NotNull Location location, @NotNull Material material, @NotNull PlaceType placeType) {
        this.setBlockInternal(location.getBlock(), XMaterial.matchXMaterial(material), placeType);
    }


    // ---------------------------------------------------------------------------------------- //
    //                                    UTIL METHODS                                          //
    // ---------------------------------------------------------------------------------------- //
    /**
     * Always available < v1.13
     */
    @SuppressWarnings("deprecation")
    public final int legacyGetCombined(XMaterial xMaterial) {
        assert xMaterial.parseMaterial() != null;
        return this.legacyGetCombined(xMaterial.parseMaterial().getId(), xMaterial.getData());
    }
    public final int legacyGetCombined(int id, byte data) {
        return id + (data << 12);
    }
}
