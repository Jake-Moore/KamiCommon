package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.abstraction.hoveritem.AbstractHoverEvent;
import com.kamikazejam.kamicommon.nms.hoveritem.HoverEvent_1_16_R2;
import com.kamikazejam.kamicommon.nms.hoveritem.HoverEvent_1_8_R1;
import org.jetbrains.annotations.NotNull;

/**
 * !!! Gradle Compatability Requires this module to be set to Java16 !!!
 * WE ARE BUILDING FOR Java 8, do not use any Java 9+ features
 */
public class HoverEventProvider extends Provider<AbstractHoverEvent> {
    @Override
    protected @NotNull AbstractHoverEvent provide(double nmsDouble, String ignored) {
        // up to 1.16.1 uses BaseComponent[] as the second parameter of HoverEvent
        if (nmsDouble <= 1161) {
            return new HoverEvent_1_8_R1();
        }else {
            return new HoverEvent_1_16_R2();
        }
    }
}
