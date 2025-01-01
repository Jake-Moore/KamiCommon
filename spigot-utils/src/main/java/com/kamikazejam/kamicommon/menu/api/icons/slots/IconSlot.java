package com.kamikazejam.kamicommon.menu.api.icons.slots;

import com.kamikazejam.kamicommon.menu.Menu;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface IconSlot {
    Set<Integer> get(@NotNull Menu menu);
    @NotNull IconSlot copy();
}
