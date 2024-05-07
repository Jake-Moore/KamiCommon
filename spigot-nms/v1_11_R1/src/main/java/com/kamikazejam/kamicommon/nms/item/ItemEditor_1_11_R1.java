package com.kamikazejam.kamicommon.nms.item;

import com.kamikazejam.kamicommon.nms.abstraction.item.AbstractItemEditor;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ItemEditor_1_11_R1 extends AbstractItemEditor {
    @Override
    public ItemMeta setUnbreakable(@NotNull ItemMeta meta, boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return meta;
    }
}
