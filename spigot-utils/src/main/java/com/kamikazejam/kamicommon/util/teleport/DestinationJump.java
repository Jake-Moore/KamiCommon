package com.kamikazejam.kamicommon.util.teleport;

import com.kamikazejam.kamicommon.util.teleport.ps.PS;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class DestinationJump extends DestinationPlayer {

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public DestinationJump(String playerId) {
        super(playerId);
    }

    public DestinationJump(Object playerObject) {
        super(playerObject);
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public PS getPsInner() {
        Player player = this.getPlayer();
        if (player == null) return null;

        Location location = DestinationUtil.getJumpLocation(player);

        return PS.valueOf(location);
    }

    @Override
    public String getDesc(Object watcherObject) {
        return "Jump for " + super.getDesc(watcherObject, false);
    }

}
