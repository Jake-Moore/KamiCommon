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
    private final @Nullable String desc;

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

    public ScheduledTeleport(String teleporteeId, @NotNull Destination destination, @Nullable String desc, int delaySeconds) {
        this.teleporteeId = teleporteeId;
        this.destination = destination;
        this.desc = desc;
        this.callback = null;
        this.delaySeconds = delaySeconds;
        this.dueMillis = 0;
    }

    public ScheduledTeleport(String teleporteeId, @NotNull MixinTeleport.TeleportCallback callback, @Nullable String desc, int delaySeconds) {
        this.teleporteeId = teleporteeId;
        this.destination = null;
        this.callback = callback;
        this.desc = desc;
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
            MixinTeleport.get().teleportInternal(this.getTeleporteeId(), this.getDestination(), this.getCallback(), this.getDesc(), 0);
        } catch (KamiCommonException e) {
            MsonMessenger.get().messageOne(this.getTeleporteeId(), e.getMessage());
        }
    }
}
