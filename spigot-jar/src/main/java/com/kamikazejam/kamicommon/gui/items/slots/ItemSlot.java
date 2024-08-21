package com.kamikazejam.kamicommon.gui.items.slots;

import com.kamikazejam.kamicommon.gui.KamiMenu;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ItemSlot {
    List<Integer> get(@NotNull KamiMenu menu);
}
