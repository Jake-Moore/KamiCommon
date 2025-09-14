package com.kamikazejam.kamicommon.util.teleport;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.util.teleport.ps.PS;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull VersionedComponent getDesc(Object watcherObject) {
        return NmsAPI.getVersionedComponentSerializer().fromPlainText("Jump for ")
                .append(super.getDesc(watcherObject, false));
    }

}
