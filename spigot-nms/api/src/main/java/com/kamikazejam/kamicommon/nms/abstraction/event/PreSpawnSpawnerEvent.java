package com.kamikazejam.kamicommon.nms.abstraction.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Pre 1.12:
 * - 1.8.8 assumes that TacoSpigot is being used (best compatability, but doesn't include spawn location)
 * - Other versions just require Spigot (worse compatability however)
 * 1.13+:
 * - Requires PaperSpigot, wraps native event (best compatability)
 */
@Getter
@SuppressWarnings("unused")
public class PreSpawnSpawnerEvent extends Event implements Cancellable {

    private final @NotNull Block spawnerBlock;
    private final @Nullable Location spawnLocation;
    private final @NotNull EntityType type;

    public PreSpawnSpawnerEvent(@NotNull Block spawnerBlock, @NotNull EntityType type, @Nullable Location spawnLocation) {
        this.spawnerBlock = spawnerBlock;
        this.spawnLocation = spawnLocation;
        this.type = type;
    }

    private static final HandlerList HANDLERS = new HandlerList();
    public static HandlerList getHandlerList() {
        return PreSpawnSpawnerEvent.HANDLERS;
    }
    public HandlerList getHandlers() {
        return PreSpawnSpawnerEvent.HANDLERS;
    }

    @Setter
    private boolean cancelled;

}

