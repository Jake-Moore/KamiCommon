package com.kamikazejam.kamicommon.configuration.loader;

import com.cryptomorin.xseries.XMaterial;
import com.kamikazejam.kamicommon.util.Preconditions;
import com.kamikazejam.kamicommon.util.nms.MaterialFlatteningUtil;
import com.kamikazejam.kamicommon.yaml.spigot.ConfigurationSection;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ItemTypeLoader {
    /**
     * Try to load an {@link XMaterial} from a configuration section which defines either a {@code material} or {@code type} key.<br>
     * <br>
     * If the type cannot be parsed (for example if neither key can be found, or the value is invalid), this method will return {@code null}.<br>
     * <br>
     * NOTE: This method supports Pre-1.13 (legacy) materials which used a data value for colors and variants.<br>
     * It will attempt to load a {@code data} key from the section, which if found, will be used as a variant data value.<br>
     * This is only necessary for legacy material names such as "WOOL", "STAINED_GLASS", "LOG", etc.<br>
     * If not found, it will default to 0.<br>
     * IMPORTANT: This is different from the {@code damage} key, which is used for raw durability modification on items like tools and armor.<br>
     *
     * @param section The configuration section to load from, and where the sub-keys are expected to be found.
     */
    @Nullable
    public static XMaterial loadType(@NotNull ConfigurationSection section) {
        Preconditions.checkNotNull(section, "ConfigurationSection cannot be null");

        // Extract the String Value of the material
        @Nullable String strValue = null;
        if (section.isString("material")) {
            strValue = section.getString("material");
        } else if (section.isString("type")) {
            strValue = section.getString("type");
        }
        if (strValue == null) {
            return null;
        }
        strValue = strValue.toUpperCase().strip();

        // Extract an optional data value (for legacy materials)
        @Nullable Integer data = null;
        if (section.isInt("data")) {
            data = section.getInt("data");
        }

        return loadTypeByString(strValue, data);
    }

    /**
     * Try to load an {@link XMaterial} from a type string and optional data value.<br>
     * <br>
     * If the type cannot be parsed (for example if the string is not a valid material), this method will return {@code null}.<br>
     * <br>
     * NOTE: This method supports Pre-1.13 (legacy) materials which used a data value for colors and variants.<br>
     * It will attempt to load a {@code data} key from the section, which if found, will be used as a variant data value.<br>
     * This is only necessary for legacy material names such as "WOOL", "STAINED_GLASS", "LOG", etc.<br>
     * If not found, it will default to 0.<br>
     * IMPORTANT: This is different from the {@code damage} key, which is used for raw durability modification on items like tools and armor.<br>
     *
     * @param materialString The string value of the material, cannot be null.
     * @param data           An optional data value for legacy materials, can be null.
     */
    public static @Nullable XMaterial loadTypeByString(@NotNull String materialString, @Nullable Integer data) {
        Preconditions.checkNotNull(materialString, "Material string cannot be null");

        // Parse this string into a Material (if available)
        @Nullable Material mat = parseMaterial(materialString);
        if (mat != null && (data == null || data == 0)) {
            // As long as we don't want a data value, we're done & we can use this Material
            return XMaterial.matchXMaterial(mat);
        }

        // Parse this string into an XMaterial, this is used as a last resort
        @Nullable XMaterial defaultParse = parseXMaterial(materialString);

        // XMaterial names are latest (i.e. 1.21 or whatever) Material values, meaning if our string was "WOOL"
        // It won't return an XMaterial.WOOL (that doesn't exist)
        // Meaning materials with colors should never pass this check, and this is only for simple materials like
        // XMaterial.BUCKET which has a Material.BUCKET to match
        if (defaultParse != null && defaultParse.name().equalsIgnoreCase(materialString) && (data == null || data == 0)) {
            return defaultParse;
        }

        // Now, we have a weird material name, or a material with a data value
        // We need to evaluate the correct XMaterial, which we can only do if we have an original material string
        // Why do we need to do this? Because when matching "WOOL" to an XMaterial, xseries will pick a colored wool i.e. BLACK_WOOL
        //   (due to the way they handle legacy names, all wool colors are mapped to "WOOL" and it just picks the first one it finds)
        // But if we have a data value (or even data value 0) we know that we want a specific wool color, and need a different enum value
        //   i.e. XMaterial.RED_WOOL

        // Let's try to fetch an XMaterial that corresponds to this material string & data
        // Data should have already been loaded prior to calling this
        Optional<XMaterial> o = MaterialFlatteningUtil.findMaterialAndDataMapping(materialString, (byte) (data == null ? 0 : data));
        // Fall back to the original XMaterial if we can't find a specific colored match
        return o.orElse(defaultParse);
    }

    private static @Nullable XMaterial parseXMaterial(@NotNull String mat) {
        try {
            return XMaterial.matchXMaterial(mat).orElse(null);
        } catch (Throwable t) {
            return null;
        }
    }

    private static @Nullable Material parseMaterial(@NotNull String mat) {
        try {
            return Material.getMaterial(mat);
        } catch (Throwable t) {
            return null;
        }
    }
}
