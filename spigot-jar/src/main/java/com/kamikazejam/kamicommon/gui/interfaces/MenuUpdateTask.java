package com.kamikazejam.kamicommon.gui.interfaces;

import com.kamikazejam.kamicommon.gui.KamiMenu;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface MenuUpdateTask {

    void onUpdate(@NotNull KamiMenu menu);

    int getLoopTicks();

}
