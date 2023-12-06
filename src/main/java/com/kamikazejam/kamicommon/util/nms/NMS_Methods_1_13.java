package com.kamikazejam.kamicommon.util.nms;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

@SuppressWarnings("unused")
public class NMS_Methods_1_13 {
    public static String getNamespaced(Enchantment enchantment) {
        return enchantment.getKey().getKey();
    }

    public static Enchantment getEnchantmentByKey(String key) {
        return Enchantment.getByKey(NamespacedKey.minecraft(key));
    }
}
