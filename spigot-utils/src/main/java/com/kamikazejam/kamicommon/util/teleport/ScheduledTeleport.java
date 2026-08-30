package com.kamikazejam.kamicommon.util.teleport;

import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.util.KUtil;
import com.kamikazejam.kamicommon.util.engine.EngineScheduledTeleport;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.util.mixin.MixinTeleport;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@SuppressWarnings({"unused", "SpellCheckingInspection", "UnusedReturnValue"})
public class ScheduledTeleport implements Runnable {
    // -------------------------------------------- //
    // FIELDS & RAW-DATA ACCESS
    // -------------------------------------------- //

    private final String teleporteeId;
    private final @Nullable VersionedComponent desc;

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

    public ScheduledTeleport(String teleporteeId, @NotNull Destination destination, @Nullable VersionedComponent desc, int delaySeconds) {
        this.teleporteeId = teleporteeId;
        this.destination = destination;
        this.desc = desc;
        this.callback = null;
        this.delaySeconds = delaySeconds;
        this.dueMillis = 0;
    }

    public ScheduledTeleport(String teleporteeId, @NotNull MixinTeleport.TeleportCallback callback, @Nullable VersionedComponent desc, int delaySeconds) {
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
            CommandSender sender = KUtil.getSender(this.getTeleporteeId());
            if (sender != null && e.getComponent() != null) {
                e.getComponent().sendTo(sender);
            }
        }
    }
}
