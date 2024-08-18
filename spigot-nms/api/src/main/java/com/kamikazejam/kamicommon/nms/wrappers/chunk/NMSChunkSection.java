package com.kamikazejam.kamicommon.nms.wrappers.chunk;

import com.kamikazejam.kamicommon.nms.wrappers.NMSObject;
import org.bukkit.Material;

public interface NMSChunkSection extends NMSObject {
    void setType(int x, int y, int z, Material material);
    boolean isEmpty();
}
