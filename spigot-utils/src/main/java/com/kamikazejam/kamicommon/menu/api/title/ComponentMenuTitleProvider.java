package com.kamikazejam.kamicommon.menu.api.title;

import com.kamikazejam.kamicommon.menu.Menu;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ComponentMenuTitleProvider {
    /**
     * A provider for the Menu title when the {@link Menu} is created.<br>
     * @param player The player viewing this menu, for context.
     * @return The title of the menu.
     */
    @NotNull
    VersionedComponent getTitle(@NotNull Player player);

    /**
     * @deprecated Construct your own {@link ComponentMenuTitleProvider} using {@link VersionedComponent} instead.
     */
    @Deprecated
    static @NotNull ComponentMenuTitleProvider fromLegacy(@NotNull MenuTitleProvider provider) {
        return (player) -> NmsAPI.getVersionedComponentSerializer().fromLegacySection(provider.getTitle(player));
    }
}
