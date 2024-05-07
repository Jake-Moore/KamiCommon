package com.kamikazejam.kamicommon.nms.abstraction.item;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class AbstractItemEditor {
    /**
     * @return ItemMeta for chaining
     */
    public abstract ItemMeta setUnbreakable(@NotNull ItemMeta meta, boolean unbreakable);
}
