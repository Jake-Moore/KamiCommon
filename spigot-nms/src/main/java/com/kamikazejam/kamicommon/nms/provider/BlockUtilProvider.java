package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.abstraction.block.IBlockUtil;
import com.kamikazejam.kamicommon.nms.block.*;
import org.jetbrains.annotations.NotNull;

public class BlockUtilProvider extends Provider<IBlockUtil> {
    @Override
    protected @NotNull IBlockUtil provide(double ignored, String nmsVersion) {
        //TODO: Add new versions as they come out
        switch(nmsVersion) {
            case "v1_8_R1":
                return new BlockUtil1_8_R1();
            case "v1_8_R2":
                return new BlockUtil1_8_R2();
            case "v1_8_R3":
                return new BlockUtil1_8_R3();
            case "v1_9_R1":
                return new BlockUtil1_9_R1();
            case "v1_9_R2":
                return new BlockUtil1_9_R2();
            case "v1_10_R1":
                return new BlockUtil1_10_R1();
            case "v1_11_R1":
                return new BlockUtil1_11_R1();
            case "v1_12_R1":
                return new BlockUtil1_12_R1();
            case "v1_13_R1":
                return new BlockUtil1_13_R1();
            case "v1_13_R2":
                return new BlockUtil1_13_R2();
            case "v1_14_R1":
                return new BlockUtil1_14_R1();
            case "v1_15_R1":
                return new BlockUtil1_15_R1();
            case "v1_16_R1":
                return new BlockUtil1_16_R1();
            case "v1_16_R2":
                return new BlockUtil1_16_R2();
            case "v1_16_R3":
                return new BlockUtil1_16_R3();

            // I think this works, not sure
            case "v1_17_R1":
                return new BlockUtil1_17_R1();
            case "v1_18_R1":
                return new BlockUtil1_18_R1();
            case "v1_18_R2":
                return new BlockUtil1_18_R2();
            case "v1_19_R1":
                return new BlockUtil1_19_R1();
            case "v1_19_R2":
                return new BlockUtil1_19_R2();
            case "v1_19_R3":
                return new BlockUtil1_19_R3();
            case "v1_20_R1":
                return new BlockUtil1_20_R1();
            case "v1_20_R2":
                return new BlockUtil1_20_R2();
            case "v1_20_R3":
                return new BlockUtil1_20_R3();
        }
        throw new IllegalArgumentException(nmsVersion + " isn't a supported version!");
    }
}
