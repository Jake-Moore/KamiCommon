package com.kamikazejam.kamicommon.menu.api.icons.slots;

import com.kamikazejam.kamicommon.menu.api.struct.size.MenuSize;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface IconSlot {
    Set<Integer> get(@NotNull MenuSize size);
    @NotNull IconSlot copy();
}
