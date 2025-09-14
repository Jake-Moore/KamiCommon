package com.kamikazejam.kamicommon.util.teleport;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.util.teleport.ps.PS;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull VersionedComponent getDesc(Object watcherObject) {
        return NmsAPI.getVersionedComponentSerializer().fromPlainText("There for ")
                .append(super.getDesc(watcherObject, false));
    }

}
