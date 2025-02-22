package com.kamikazejam.kamicommon.util.nms;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A Utility class to provide some helper methods when dealing with the Material Flattening changes.<br>
 * These methods make no guarantee to EXACTLY map your intentions, but they try their best.
 */
public class MaterialFlatteningUtil {
    private static final Map<String, XMaterial> unflattenedToNewMaterialMap = new HashMap<>();
    public static void initialize() {
        unflattenedToNewMaterialMap.clear();
        for (XMaterial xMaterial : XMaterial.VALUES) {
            if (xMaterial.name().endsWith("_WOOD")) { continue; }           // Skip _WOOD since we likely want _LOG instead
            if (xMaterial.name().endsWith("_WALL_HEAD")) { continue; }      // Skip _WALL_HEAD since we likely want the regular head instead
            if (xMaterial.name().endsWith("_WALL_BANNER")) { continue; }    // Skip _WALL_BANNER since we likely want the regular banner instead

            // For each one of the legacy names
            for (String legacyName : xMaterial.getLegacy()) {
                if (legacyName.equalsIgnoreCase("AIR")) { continue; } // Skip AIR since it's the default

                // Create a mapping key back to this XMaterial
                String key = legacyName + ":" + xMaterial.getData();
                if (unflattenedToNewMaterialMap.containsKey(key)) { continue; } // Let's hope the first one was the one we wanted

                unflattenedToNewMaterialMap.put(key, xMaterial);
            }
        }
    }

    @NotNull
    public static Optional<XMaterial> findMaterialAndDataMapping(@NotNull String matName, byte data) {
        // If we have no data value, and just want a material, try to find a matching Material directly
        if (data == 0) {
            // If found, convert it to XMaterial (safe/guaranteed conversion with XMaterial)
            @Nullable Material baseMaterial = Material.getMaterial(matName);
            if (baseMaterial != null) {
                return Optional.of(XMaterial.matchXMaterial(baseMaterial));
            }
        }

        String key = matName + ":" + data;
        return Optional.ofNullable(unflattenedToNewMaterialMap.get(key));
    }
}
