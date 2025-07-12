package com.kamikazejam.kamicommon.menu.api.clicks.transform;

import com.kamikazejam.kamicommon.menu.Menu;
import com.kamikazejam.kamicommon.menu.api.clicks.MenuClick;
import com.kamikazejam.kamicommon.menu.api.clicks.data.MenuClickData;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class MenuClickTransform<M extends Menu<M>> {
    private final @NotNull MenuClick<M> click;

    public MenuClickTransform(@NotNull MenuClick<M> click) {
        this.click = click;
    }

    public final void process(@NotNull MenuClickData<M> data) {
        // Play sound for click
        data.getIcon().playClickSound(data.getPlayer());

        // Process click callback
        click.onClick(data);
    }
}