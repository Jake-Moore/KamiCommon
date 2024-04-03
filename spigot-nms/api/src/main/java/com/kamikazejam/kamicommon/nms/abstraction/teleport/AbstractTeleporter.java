package com.kamikazejam.kamicommon.nms.abstraction.teleport;

import org.bukkit.Location;
import org.bukkit.entity.Player;


public abstract class AbstractTeleporter {
    public abstract void teleportWithoutEvent(Player player, Location location);
}
