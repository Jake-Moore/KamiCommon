package com.kamikazejam.kamicommon.util.teleport;

import com.kamikazejam.kamicommon.util.teleport.ps.PS;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class DestinationThat extends DestinationPlayer {

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public DestinationThat(String playerId) {
        super(playerId);
    }

    public DestinationThat(Object playerObject) {
        super(playerObject);
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public PS getPsInner() {
        Player player = this.getPlayer();
        if (player == null) return null;

        Location location = DestinationUtil.getThatLocation(player);

        return PS.valueOf(location);
    }

    @Override
    public String getDesc(Object watcherObject) {
        return "That for " + super.getDesc(watcherObject, false);
    }

}
