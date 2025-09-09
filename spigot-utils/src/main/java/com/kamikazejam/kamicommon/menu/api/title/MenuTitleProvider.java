package com.kamikazejam.kamicommon.menu.api.title;

import com.kamikazejam.kamicommon.menu.Menu;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @deprecated Replace with {@link ComponentMenuTitleProvider} using {@link VersionedComponent}
 */
@Deprecated
public interface MenuTitleProvider {
    /**
     * A provider for the Menu title when the {@link Menu} is created.<br>
     * @param player The player viewing this menu, for context.
     * @return The title of the menu.
     */
    @NotNull
    String getTitle(@NotNull Player player);
}
