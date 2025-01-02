package com.kamikazejam.kamicommon.util;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ItemUtil {
    /**
     * A simple custom implementation of the traditional {@link ItemStack#isSimilar(ItemStack)} method.<br>
     * This method compares the type, amount, durability, name, lore, color, and enchantments of two items.<br>
     * If all of these fields are equal, the items are considered similar.
     * @return If the items are similar
     */
    public static boolean isSimplySimilar(@Nullable ItemStack item1, @Nullable ItemStack item2) {
        //Check null, material types, and amounts
        if (item1 == null || item2 == null) { return false; }
        if (item1.getType() != item2.getType()) { return false; }
        if (item1.getAmount() != item2.getAmount()) { return false; }
        if (item1.hasItemMeta() != item2.hasItemMeta()) { return false; }

        if (XMaterial.matchXMaterial(item1).equals(XMaterial.POTION) && XMaterial.matchXMaterial(item2).equals(XMaterial.POTION) || item1.getDurability() == item2.getDurability()) {
            @Nullable ItemMeta meta1 = item1.getItemMeta();
            @Nullable ItemMeta meta2 = item2.getItemMeta();

            // Some versions can have null metas
            if (meta1 != null && meta2 != null) {
                // Compare leather colors
                if (meta1 instanceof LeatherArmorMeta && meta2 instanceof LeatherArmorMeta && !((LeatherArmorMeta) meta1).getColor().equals(((LeatherArmorMeta) meta2).getColor())) {
                    return false;
                }

                // Compare display names
                if (!meta1.hasDisplayName() || !meta2.hasDisplayName() || !meta1.getDisplayName().equals(meta2.getDisplayName())) {
                    if (meta1.hasDisplayName() && meta2.hasDisplayName()) { return false; }
                }

                // Compare lore
                if (!meta1.hasLore() || !meta2.hasLore() || !Objects.equals(meta1.getLore(), meta2.getLore())) {
                    if (meta1.hasLore() && meta2.hasLore()) { return false; }
                }
            }

            return item1.getEnchantments().equals(item2.getEnchantments());
        }
        return false;
    }
}
