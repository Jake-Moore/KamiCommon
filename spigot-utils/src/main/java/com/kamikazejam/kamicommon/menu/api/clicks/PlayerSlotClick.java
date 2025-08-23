package com.kamikazejam.kamicommon.menu.api.clicks;

import com.kamikazejam.kamicommon.menu.Menu;
import com.kamikazejam.kamicommon.menu.api.clicks.data.PlayerClickData;
import org.jetbrains.annotations.NotNull;

public interface PlayerSlotClick<M extends Menu<M>> {
    void onClick(@NotNull PlayerClickData<M> data);
}
