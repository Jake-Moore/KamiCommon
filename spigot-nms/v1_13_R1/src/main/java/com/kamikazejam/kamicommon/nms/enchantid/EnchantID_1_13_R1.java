package com.kamikazejam.kamicommon.nms.enchantid;

import com.kamikazejam.kamicommon.nms.abstraction.enchantid.AbstractEnchantID;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

public class EnchantID_1_13_R1 extends AbstractEnchantID {
    @Override
    public @NotNull String getNamespaced(Enchantment enchantment) {
        return enchantment.getKey().getKey();
    }
}
