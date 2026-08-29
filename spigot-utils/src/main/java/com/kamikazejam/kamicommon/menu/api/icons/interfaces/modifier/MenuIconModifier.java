package com.kamikazejam.kamicommon.menu.api.icons.interfaces.modifier;

import com.kamikazejam.kamicommon.menu.api.icons.MenuIcon;
import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a modifier for a {@link MenuIcon}.
 */
/**
 * <b>Do not implement this outside KamiCommon.</b> This was a {@code sealed} hierarchy until
 * spigot-utils dropped to Java 8 so that 1.8.x servers could load it; {@code sealed} is Java 17
 * and has no Java 8 spelling. The closed set is still enforced <i>within</i> the library by the
 * {@code verifySealedHierarchies} build task, which fails if an implementation appears that is
 * not on the permitted list (StaticIconModifier, StatefulIconModifier). Nothing can enforce it in your code, hence this annotation.
 */
@ApiStatus.NonExtendable
public interface MenuIconModifier {
    // Nothing Needed Here, this interface exists as an abstraction for one of the permitted sealed interfaces
}
