package com.kamikazejam.kamicommon.menu.items.slots;

import com.kamikazejam.kamicommon.menu.KamiMenu;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface ItemSlot {
    Set<Integer> get(@NotNull KamiMenu menu);
    @NotNull ItemSlot copy();
}
