package com.kamikazejam.kamicommon.gui.items.interfaces;

import com.kamikazejam.kamicommon.gui.clicks.MenuClick;
import com.kamikazejam.kamicommon.gui.clicks.MenuClickEvent;
import com.kamikazejam.kamicommon.gui.clicks.MenuClickPage;
import org.jetbrains.annotations.NotNull;

/**
 * A reduced set of methods for managing a MenuItem.
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public interface IMenuItem {
    @NotNull
    IMenuItem setMenuClick(@NotNull MenuClick click);
    @NotNull
    IMenuItem setMenuClick(@NotNull MenuClickPage click);
    @NotNull
    IMenuItem setMenuClick(@NotNull MenuClickEvent click);
    @NotNull
    IMenuItem setAutoUpdate(@NotNull IBuilderModifier modifier, int tickInterval);
    @NotNull
    IMenuItem setModifier(@NotNull IBuilderModifier modifier);
}
