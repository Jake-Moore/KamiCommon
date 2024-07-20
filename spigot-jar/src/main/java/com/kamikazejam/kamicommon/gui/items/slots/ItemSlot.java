package com.kamikazejam.kamicommon.gui.items.slots;

import com.kamikazejam.kamicommon.gui.interfaces.Menu;
import com.kamikazejam.kamicommon.gui.page.PageBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ItemSlot {
    List<Integer> get(@NotNull Menu menu);
}
