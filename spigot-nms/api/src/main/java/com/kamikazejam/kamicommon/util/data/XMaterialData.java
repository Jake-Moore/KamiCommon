package com.kamikazejam.kamicommon.util.data;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

/**
 * pseudo-MaterialData using XSeries XMaterial for cross-version compatibility
 */
@SuppressWarnings("unused")
@Getter @Setter
@Accessors(chain = true)
public class XMaterialData {
    // Used for pre-1.13
    private byte data;
    private @NotNull XMaterial material;

    public XMaterialData(@NotNull XMaterial material) {
        // Default to whatever the XMaterial data value needs to set that block
        this.material = material;
        this.data = material.getData();
    }

    public XMaterialData(@NotNull XMaterial material, int data) {
        // Use the specified data value
        this.material = material;
        this.data = (byte) data;
    }
}
