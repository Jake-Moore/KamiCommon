package com.kamikazejam.kamicommon.util.teleport;

import com.kamikazejam.kamicommon.util.StringUtil;
import com.kamikazejam.kamicommon.util.id.IdUtilLocal;
import com.kamikazejam.kamicommon.util.mixin.MixinDisplayName;
import com.kamikazejam.kamicommon.util.mixin.MixinPlayed;
import com.kamikazejam.kamicommon.util.mixin.MixinSenderPs;
import com.kamikazejam.kamicommon.util.teleport.ps.PS;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Setter
@Getter
public class DestinationPlayer extends DestinationAbstract {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    protected String playerId;

    public void setPlayer(Object playerObject) {
        this.playerId = IdUtilLocal.getId(playerObject);
    }

    public CommandSender getSender() {
        return IdUtilLocal.getSender(this.playerId);
    }

    public Player getPlayer() {
        return IdUtilLocal.getPlayer(this.playerId);
    }

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public DestinationPlayer(String playerId) {
        this.setPlayerId(playerId);
    }

    public DestinationPlayer(Object playerObject) {
        this.setPlayer(playerObject);
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public PS getPsInner() {
        return MixinSenderPs.get().getSenderPs(this.playerId);
    }

    @Override
    public String getDesc(Object watcherObject) {
        return this.getDesc(watcherObject, true);
    }

    private static final String offline = StringUtil.t(" &c[Offline]");
    public String getDesc(Object watcherObject, boolean prefix) {
        String ret = "";

        // Player Prefix
        if (prefix) {
            ret += "Player ";
        }

        // Display Name
        ret += MixinDisplayName.get().getDisplayName(this.getPlayerId(), watcherObject);

        // Offline Suffix
        if (MixinPlayed.get().isOffline(this.getPlayerId())) {
            ret += offline;
        }

        return ret;
    }

}
