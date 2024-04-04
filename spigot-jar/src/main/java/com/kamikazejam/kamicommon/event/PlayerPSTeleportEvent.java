package com.kamikazejam.kamicommon.event;

import com.kamikazejam.kamicommon.util.teleport.Destination;
import com.kamikazejam.kamicommon.util.teleport.ps.PS;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class PlayerPSTeleportEvent extends KamiCommonEvent {
    // -------------------------------------------- //
    // REQUIRED EVENT CODE
    // -------------------------------------------- //

    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    protected final String teleporteeId;

    protected final PS origin;

    protected Destination destination;

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public PlayerPSTeleportEvent(String teleporteeId, PS origin, Destination destination) {
        this.teleporteeId = teleporteeId;
        this.origin = origin;
        this.destination = destination;
    }

}
