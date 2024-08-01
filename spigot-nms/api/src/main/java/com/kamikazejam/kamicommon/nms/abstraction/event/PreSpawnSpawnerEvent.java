package com.kamikazejam.kamicommon.nms.abstraction.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Pre 1.12:
 * - 1.8.8 assumes that TacoSpigot is being used (best compatability)
 * - Other versions just require Spigot (worse compatability however)
 * 1.13+:
 * - Requires PaperSpigot, wraps native event (best compatability)
 */
@Getter
@SuppressWarnings("unused")
public class PreSpawnSpawnerEvent extends BlockEvent implements Cancellable {

    private final @NotNull Location location;
    private final @NotNull EntityType type;

    public PreSpawnSpawnerEvent(@NotNull Block theBlock, @NotNull EntityType type) {
        super(theBlock);
        this.location = theBlock.getLocation();
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

