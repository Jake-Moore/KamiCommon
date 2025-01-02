package com.kamikazejam.kamicommon.menu.api.icons.interfaces.modifier;

import com.kamikazejam.kamicommon.item.IBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A stateful modifier for an icon's {@link IBuilder} it wants to place in a menu.<br>
 * The {@link #modify} method also provides the existing item in the menu (if it exists) and the {@link Player} viewing the menu.
 */
public non-sealed interface StatefulIconModifier extends MenuIconModifier {
    /**
     * A simple modify method to edit the state of the builder for an Auto Updating icon.<br>
     * There is no return value because the builder is modified in place.
     * @param initialBuilder is the initial builder of the icon, as configured prior to opening the menu.
     * @param currentItem is the current item in the menu, if it exists. (null if the icon has not been placed yet)
     * @param player is the player who is viewing the menu.
     */
    void modify(@NotNull IBuilder initialBuilder, @Nullable ItemStack currentItem, @NotNull Player player);
}
