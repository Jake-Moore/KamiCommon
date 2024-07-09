package com.kamikazejam.kamicommon.util.data;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.util.data.types.SlabType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

/**
 * pseudo-BlockData using XSeries for cross-version compatibility
 * Provides methods for both pre-1.13 and 1.13+ BlockUtil support
 * <p>
 * BlockUtil handles the BlockData properties in IBlockUtil1_13
 */
@SuppressWarnings("unused")
@Getter @Accessors(chain = true)
public class XBlockData {
    private final XMaterialData materialData;
    // 1.13+ BlockData properties
    @Setter private @Nullable Integer level = null;
    @Setter private @Nullable SlabType slabType = null;

    public XBlockData(XMaterial material) {
        this.materialData = new XMaterialData(material);
    }
    public XBlockData(XMaterial material, int data) {
        this.materialData = new XMaterialData(material, data);
    }
    public XBlockData(XMaterialData materialData) {
        this.materialData = materialData;
    }



    // ---------------------------------------------------------- //
    //           Static instances for common BlockData            //
    // ---------------------------------------------------------- //

    public static XBlockData SOURCE_WATER = new XBlockData(XMaterial.WATER);
    // data 8 -> pre-1.13      level 8 -> 1.13+
    public static XBlockData STATIONARY_WATER = new XBlockData(XMaterial.WATER, 8).setLevel(8);
    // data 8 -> pre-1.13      level 8 -> 1.13+
    public static XBlockData UPPER_STONE_SLAB = new XBlockData(XMaterial.STONE_BRICK_SLAB, 8).setSlabType(SlabType.TOP);
    // SAND:1 -> pre-1.13      RED_SAND -> 1.13+
    public static XBlockData RED_SAND = new XBlockData(XMaterial.RED_SAND);
}
