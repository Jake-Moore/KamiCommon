package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import com.kamikazejam.kamicommon.nms.wrappers.NMSObject;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface NMSChunk extends NMSObject {
    @NotNull
    NMSChunkSection getSection(int y);
    @NotNull
    NMSChunkSection getOrCreateSection(int y);
    void clearTileEntities();

    void sendUpdatePacket(@NotNull Player player);
}
