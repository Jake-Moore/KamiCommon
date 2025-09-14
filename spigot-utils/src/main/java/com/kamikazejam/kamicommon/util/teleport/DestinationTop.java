package com.kamikazejam.kamicommon.util.teleport;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.util.teleport.ps.PS;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public class DestinationTop extends DestinationPlayer {

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public DestinationTop(String playerId) {
        super(playerId);
    }

    public DestinationTop(Object playerObject) {
        super(playerObject);
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public PS getPsInner() {
        Player player = this.getPlayer();
        if (player == null) return null;

        Location location = player.getLocation();
        location.setY(Objects.requireNonNull(location.getWorld()).getHighestBlockYAt(location) + 1);

        return PS.valueOf(location);
    }

    @Override
    public @NotNull VersionedComponent getDesc(Object watcherObject) {
        return NmsAPI.getVersionedComponentSerializer().fromPlainText("Top for ")
                .append(super.getDesc(watcherObject, false));
    }

}
