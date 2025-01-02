package com.kamikazejam.kamicommon.menu.api.callbacks;

import com.kamikazejam.kamicommon.menu.Menu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface MenuPostCloseCallback {
    /**
     * Called after the menu is closed. With the ability to safely re-open the menu if needed.<br>
     * See {@link Menu#reopenMenu)} for reopening the menu for the player.
     * @param player The player who closed the menu.
     * @param menu The menu that was closed
     */
    void onPostClose(@NotNull Player player, @NotNull Menu menu);
}
