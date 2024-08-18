package com.kamikazejam.kamicommon.nms.wrappers.world;

import com.kamikazejam.kamicommon.nms.wrappers.NMSObject;
import com.kamikazejam.kamicommon.nms.wrappers.chunk.NMSChunkProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface NMSWorld extends NMSObject {
    @NotNull
    NMSChunkProvider getChunkProvider();
    int getMinHeight();
    int getMaxHeight();
    void refreshBlockAt(@NotNull Player player, int x, int y, int z);
}
