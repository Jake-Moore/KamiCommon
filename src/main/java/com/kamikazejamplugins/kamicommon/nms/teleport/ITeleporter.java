package com.kamikazejamplugins.kamicommon.nms.teleport;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class ITeleporter {
    public abstract void teleportWithoutEvent(Player player, Location location);
}
