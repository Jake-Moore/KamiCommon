package com.kamikazejam.kamicommon.nms.abstraction.enchantid;

import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractEnchantID {
    public abstract @NotNull String getNamespaced(Enchantment enchantment);
}
