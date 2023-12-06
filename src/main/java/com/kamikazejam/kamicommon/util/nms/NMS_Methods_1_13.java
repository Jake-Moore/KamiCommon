package com.kamikazejam.kamicommon.util.nms;

import org.bukkit.enchantments.Enchantment;

@SuppressWarnings("unused")
public class NMS_Methods_1_13 {
    static String getNamespaced(Enchantment enchantment) {
        return enchantment.getKey().getKey();
    }
}
