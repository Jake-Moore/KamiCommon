package com.kamikazejam.kamicommon.util.nms;

import com.kamikazejam.kamicommon.nms.NmsManager;
import org.bukkit.enchantments.Enchantment;

@SuppressWarnings("unused")
public class NMS_Methods {
    @SuppressWarnings("deprecation")
    public static String getNamespaced(Enchantment enchantment) {
        // If after 1.13, we have access to namespaced keys
        if (NmsManager.getFormattedNmsDouble() >= 1130) {
            return NMS_Methods_1_13.getNamespaced(enchantment);
        }else {
            return "minecraft:" + enchantment.getName().toLowerCase();
        }
    }
}
