package com.kamikazejam.kamicommon.util.teleport;

import com.kamikazejam.kamicommon.util.engine.EngineScheduledTeleport;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.util.mixin.MixinTeleport;
import com.kamikazejam.kamicommon.util.mson.MsonMessenger;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class ScheduledTeleport implements Runnable {
    // -------------------------------------------- //
    // FIELDS & RAW-DATA ACCESS
    // -------------------------------------------- //

    private final String teleporteeId;

    private final @Nullable Destination destination;
    private final @Nullable MixinTeleport.TeleportCallback callback;

    private final int delaySeconds;

    @Setter
    private long dueMillis;

    public boolean isDue(long now) {
        return now >= this.dueMillis;
    }

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public ScheduledTeleport(String teleporteeId, @NotNull Destination destination, int delaySeconds) {
        this.teleporteeId = teleporteeId;
        this.destination = destination;
        this.callback = null;
        this.delaySeconds = delaySeconds;
        this.dueMillis = 0;
    }

    public ScheduledTeleport(String teleporteeId, @NotNull MixinTeleport.TeleportCallback callback, int delaySeconds) {
        this.teleporteeId = teleporteeId;
        this.destination = null;
        this.callback = callback;
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
            if (this.getDestination() != null) {
                MixinTeleport.get().teleport(this.getTeleporteeId(), this.getDestination(), 0);
            }else if (this.getCallback() != null) {
                this.getCallback().run();
            }
        } catch (KamiCommonException e) {
            MsonMessenger.get().messageOne(this.getTeleporteeId(), e.getMessage());
        }
    }
}
