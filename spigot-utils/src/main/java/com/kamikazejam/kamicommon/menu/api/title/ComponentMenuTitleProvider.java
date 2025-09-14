package com.kamikazejam.kamicommon.menu.api.title;

import com.kamikazejam.kamicommon.menu.Menu;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.util.LegacyColors;
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
        return (player) -> {
            // legacy behavior was to automatically translate the alternate ampersand color codes to section symbols
            String legacyTitle = provider.getTitle(player);
            return NmsAPI.getVersionedComponentSerializer().fromLegacySection(LegacyColors.t(legacyTitle));
        };
    }
}
