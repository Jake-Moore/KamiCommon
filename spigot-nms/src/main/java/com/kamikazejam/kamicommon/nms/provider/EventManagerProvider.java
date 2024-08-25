package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.abstraction.event.EventManager;
import com.kamikazejam.kamicommon.nms.event.EventManager_1_8_R1;
import com.kamikazejam.kamicommon.nms.event.EventManager_1_9_R1;
import org.jetbrains.annotations.NotNull;

/**
 * !!! Gradle Compatability Requires this module to be set to Java16 !!!
 * WE ARE BUILDING FOR Java 8, do not use any Java 9+ features
 */
public class EventManagerProvider extends Provider<EventManager> {
    @Override
    protected @NotNull EventManager provide(int ver) {
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        // Just an adapter for offhand changes in 1.9
        // Should be compatible with every version
        if (ver < f("1.9")) {
            return new EventManager_1_8_R1();
        }else {
            return new EventManager_1_9_R1();
        }
    }
}
