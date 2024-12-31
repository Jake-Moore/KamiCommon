package com.kamikazejam.kamicommon.menu.items.slots;

import com.kamikazejam.kamicommon.menu.Menu;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface ItemSlot {
    Set<Integer> get(@NotNull Menu menu);
    @NotNull ItemSlot copy();
}
