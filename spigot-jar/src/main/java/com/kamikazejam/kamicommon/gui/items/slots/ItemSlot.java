package com.kamikazejam.kamicommon.gui.items.slots;

import com.kamikazejam.kamicommon.gui.page.PageBuilder;

import java.util.List;

public interface ItemSlot {
    List<Integer> get(PageBuilder<?> builder);
}
