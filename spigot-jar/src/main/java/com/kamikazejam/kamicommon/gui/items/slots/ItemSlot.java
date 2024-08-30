package com.kamikazejam.kamicommon.gui.items.slots;

import com.kamikazejam.kamicommon.gui.KamiMenu;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface ItemSlot {
    Set<Integer> get(@NotNull KamiMenu menu);
    @NotNull ItemSlot copy();
}
