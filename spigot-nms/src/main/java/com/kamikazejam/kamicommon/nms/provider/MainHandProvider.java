package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.abstraction.mainhand.AbstractMainHand;
import com.kamikazejam.kamicommon.nms.mainhand.MainHand_1_8_R1;
import com.kamikazejam.kamicommon.nms.mainhand.MainHand_1_9_R1;
import org.jetbrains.annotations.NotNull;

public class MainHandProvider extends Provider<AbstractMainHand> {
    @Override
    protected @NotNull AbstractMainHand provide(int ver) {
        if (ver <= 1090) {
            return new MainHand_1_8_R1();
        }
        return new MainHand_1_9_R1();
    }
}
