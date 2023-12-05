package com.kamikazejam.kamicommon.util.teleport;

import com.kamikazejam.kamicommon.util.engine.EngineScheduledTeleport;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.util.mixin.MixinTeleport;
import com.kamikazejam.kamicommon.util.mson.MsonMessenger;
import lombok.Getter;

@Getter
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class ScheduledTeleport implements Runnable {
    // -------------------------------------------- //
    // FIELDS & RAW-DATA ACCESS
    // -------------------------------------------- //

    private final String teleporteeId;

    private final Destination destination;

    private final int delaySeconds;

    private long dueMillis;

    public void setDueMillis(long dueMillis) {
        this.dueMillis = dueMillis;
    }

    public boolean isDue(long now) {
        return now >= this.dueMillis;
    }

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public ScheduledTeleport(String teleporteeId, Destination destination, int delaySeconds) {
        this.teleporteeId = teleporteeId;
        this.destination = destination;
        this.delaySeconds = delaySeconds;
        this.dueMillis = 0;
    }

    // -------------------------------------------- //
    // SCHEDULING
    // -------------------------------------------- //

    public boolean isScheduled() {
        return EngineScheduledTeleport.get().isScheduled(this);
    }

    public ScheduledTeleport schedule() {
        return EngineScheduledTeleport.get().schedule(this);
    }

    public boolean unschedule() {
        return EngineScheduledTeleport.get().unschedule(this);
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public void run() {
        this.unschedule();
        try {
            MixinTeleport.get().teleport(this.getTeleporteeId(), this.getDestination(), 0);
        } catch (KamiCommonException e) {
            MsonMessenger.get().messageOne(this.getTeleporteeId(), e.getMessage());
        }
    }

}
