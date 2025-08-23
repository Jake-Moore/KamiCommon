package com.kamikazejam.kamicommon.menu.api.clicks;

import com.kamikazejam.kamicommon.menu.Menu;
import com.kamikazejam.kamicommon.menu.api.clicks.data.MenuClickData;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface MenuClick<M extends Menu<M>> {
    void onClick(@NotNull MenuClickData<M> data);
}
