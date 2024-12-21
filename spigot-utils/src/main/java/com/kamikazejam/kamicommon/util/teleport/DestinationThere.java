package com.kamikazejam.kamicommon.util.teleport;

import com.kamikazejam.kamicommon.util.teleport.ps.PS;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class DestinationThere extends DestinationPlayer {

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public DestinationThere(String playerId) {
        super(playerId);
    }

    public DestinationThere(Object playerObject) {
        super(playerObject);
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public PS getPsInner() {
        Player player = this.getPlayer();
        if (player == null) return null;

        Location location = DestinationUtil.getThereLocation(player);

        return PS.valueOf(location);
    }

    @Override
    public String getDesc(Object watcherObject) {
        return "There for " + super.getDesc(watcherObject, false);
    }

}
