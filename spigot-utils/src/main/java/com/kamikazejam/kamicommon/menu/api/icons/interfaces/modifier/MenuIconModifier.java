package com.kamikazejam.kamicommon.menu.api.icons.interfaces.modifier;

import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;

/**
 * Represents a modifier for a {@link MenuIcon}.
 */
public sealed interface MenuIconModifier permits StaticIconModifier, StatefulIconModifier {
    // Nothing Needed Here, this interface exists as an abstraction for one of the permitted sealed interfaces
}
