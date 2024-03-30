package com.kamikazejam.kamicommon.event;

import com.kamikazejam.kamicommon.util.collections.KamiMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.util.Map;

@SuppressWarnings("unused")
public abstract class KamiCommonEvent extends Event implements Runnable, Cancellable {
    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    @Getter
    private final transient Map<String, Object> customData = new KamiMap<>();

    private boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    // -------------------------------------------- //
    // OVERRIDE: RUNNABLE
    // -------------------------------------------- //

    @Override
    public void run() {
        Bukkit.getPluginManager().callEvent(this);
    }

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public KamiCommonEvent() {
        super(!Bukkit.isPrimaryThread()); // TODO - Is this incredibly dangerous?
    }

    public KamiCommonEvent(boolean isAsync) {
        super(isAsync);
    }

    // -------------------------------------------- //
    // UTIL
    // -------------------------------------------- //

    public boolean isSynchronous() {
        return !this.isAsynchronous();
    }

}

