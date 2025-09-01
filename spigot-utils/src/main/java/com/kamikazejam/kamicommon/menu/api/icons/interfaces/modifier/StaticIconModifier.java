package com.kamikazejam.kamicommon.menu.api.icons.interfaces.modifier;

import com.kamikazejam.kamicommon.item.ItemBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * A static modifier for an icon's {@link ItemBuilder} it wants to place in a menu.
 */
public non-sealed interface StaticIconModifier extends MenuIconModifier {
    /**
     * A simple modify method to edit the state of the builder.<br>
     * There is no return value because the builder is modified in place.
     * @param builder The builder to modify.
     * @return The modified builder (can be the same instance modified in place).
     */
    @NotNull
    ItemBuilder modify(@NotNull ItemBuilder builder);
}
