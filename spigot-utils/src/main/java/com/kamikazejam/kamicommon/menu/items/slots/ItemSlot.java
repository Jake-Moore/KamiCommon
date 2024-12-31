package com.kamikazejam.kamicommon.menu.items.slots;

import com.kamikazejam.kamicommon.menu.OLD_KAMI_MENU;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface ItemSlot {
    Set<Integer> get(@NotNull OLD_KAMI_MENU menu);
    @NotNull ItemSlot copy();
}
