package com.kamikazejam.kamicommon.menu.api.icons.interfaces.modifier;

import com.kamikazejam.kamicommon.item.IBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * A static modifier for an icon's {@link IBuilder} it wants to place in a menu.
 */
public non-sealed interface StaticIconModifier extends MenuIconModifier {
    /**
     * A simple modify method to edit the state of the builder.<br>
     * There is no return value because the builder is modified in place.
     */
    void modify(@NotNull IBuilder builder);
}
