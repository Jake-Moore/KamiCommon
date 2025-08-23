package com.kamikazejam.kamicommon.menu.api.clicks.transform;

import com.kamikazejam.kamicommon.menu.Menu;
import com.kamikazejam.kamicommon.menu.api.clicks.PlayerSlotClick;
import com.kamikazejam.kamicommon.menu.api.clicks.data.PlayerClickData;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class PlayerSlotClickTransform<M extends Menu<M>> {
    private final @NotNull PlayerSlotClick<M> click;

    public PlayerSlotClickTransform(@NotNull PlayerSlotClick<M> click) {
        this.click = click;
    }

    public final void process(@NotNull PlayerClickData<M> data) {
        click.onClick(data);
    }
}