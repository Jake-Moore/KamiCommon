package com.kamikazejam.kamicommon.nms.abstraction.block;

import com.kamikazejam.kamicommon.util.data.MaterialData;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

public abstract class IBlockUtil1_13 extends IBlockUtil {
    /**
     * @return BlockData if it changed (for re-applying), else null
     */
    public abstract @Nullable BlockData tryLeveled(BlockData blockData, MaterialData materialData);

    @Override
    public boolean youShouldNotBeSeeingThisIfYouExtendThisClass() { return false; }

    /**
     * @return true IFF the class provided is exactly BlockData (not a subclass)
     */
    public final boolean isBlockDataExact(BlockData blockData) {
        return blockData.getClass().isAssignableFrom(BlockData.class);
    }
}

