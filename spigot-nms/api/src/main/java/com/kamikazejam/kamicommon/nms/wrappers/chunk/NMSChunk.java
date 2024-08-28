package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import com.kamikazejam.kamicommon.nms.wrappers.NMSObject;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface NMSChunk extends NMSObject {
    @NotNull NMSChunkProvider getNMSChunkProvider();
    @NotNull Chunk getBukkitChunk();

    @NotNull
    NMSChunkSection getSection(int y);
    @NotNull
    NMSChunkSection getOrCreateSection(int y);
    void clearTileEntities();

    void sendUpdatePacket(@NotNull Player player);

    int getX();
    int getZ();

    void saveAndRefresh(boolean withUpdatePackets);

}
