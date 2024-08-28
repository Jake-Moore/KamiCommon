package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import com.kamikazejam.kamicommon.nms.wrappers.NMSObject;
import com.kamikazejam.kamicommon.nms.wrappers.world.NMSWorld;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface NMSChunkProvider extends NMSObject {
    @NotNull NMSWorld getNMSWorld();

    default boolean isForceChunkLoad() {
        // For most versions this is simply true
        return true;
    }

    default void setForceChunkLoad(boolean value) {
        // Do nothing (most versions can't change this)
    }

    void saveChunk(@NotNull NMSChunk chunk);

    @NotNull
    NMSChunk wrap(@NotNull Chunk chunk);
}
