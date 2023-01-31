package com.kamikazejamplugins.kamicommon.util;

import com.cryptomorin.xseries.XMaterial;
import lombok.Data;
import org.bukkit.Material;

@SuppressWarnings("unused")
@Data
public class MaterialData {
    private final Material material;
    private final byte data;

    public MaterialData(Material material) {
        this.material = material;
        this.data = 0;
    }

    public MaterialData(Material material, byte data) {
        this.material = material;
        this.data = data;
    }

    public MaterialData(XMaterial xMaterial) {
        this.material = xMaterial.parseMaterial();
        this.data = xMaterial.getData();
    }

    public MaterialData(XMaterial xMaterial, byte data) {
        this.material = xMaterial.parseMaterial();
        this.data = data;
    }

    @SuppressWarnings("deprecation")
    public org.bukkit.material.MaterialData toMaterialData() {
        return new org.bukkit.material.MaterialData(material, data);
    }
}
