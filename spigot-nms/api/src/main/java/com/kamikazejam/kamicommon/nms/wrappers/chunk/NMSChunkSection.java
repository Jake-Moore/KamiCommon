package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import com.kamikazejam.kamicommon.nms.wrappers.NMSObject;
import com.kamikazejam.kamicommon.util.data.XBlockData;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface NMSChunkSection extends NMSObject {
    @NotNull
    NMSChunk getNMSChunk();

    void setType(int x, int y, int z, @NotNull Material material);
    void setType(int x, int y, int z, @NotNull XBlockData blockData);
    boolean isEmpty();
}
