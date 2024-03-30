package com.kamikazejam.kamicommon.nms.abstraction.block;

import com.kamikazejam.kamicommon.util.data.MaterialData;

public abstract class IBlockUtilPre1_13 extends IBlockUtil {
    @SuppressWarnings("deprecation") // Always available < v1.13
    public final int getCombined(MaterialData data) {
        return data.getMaterial().getId() + (data.getData() << 12);
    }

    @Override
    public boolean youShouldNotBeSeeingThisIfYouExtendThisClass() { return false; }
}
