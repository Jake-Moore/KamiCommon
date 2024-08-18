package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import com.kamikazejam.kamicommon.nms.wrappers.NMSObject;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

public interface NMSChunkProvider extends NMSObject {
    default boolean isForceChunkLoad() {
        // For most versions this is simply true
        return true;
    }

    default void setForceChunkLoad(boolean value) {
        // Do nothing (most versions can't change this)
    }

    @NotNull
    NMSChunk getOrCreateChunk(int x, int z);

    void saveChunk(@NotNull NMSChunk chunk);

    @NotNull
    NMSChunk wrap(@NotNull Chunk chunk);
}
