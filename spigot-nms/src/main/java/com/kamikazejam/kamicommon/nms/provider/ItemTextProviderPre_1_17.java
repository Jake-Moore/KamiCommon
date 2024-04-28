package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.abstraction.itemtext.AbstractItemTextPre_1_17;
import com.kamikazejam.kamicommon.nms.itemtext.*;
import org.jetbrains.annotations.NotNull;

/**
 * !!! Gradle Compatability Requires this module to be set to Java16 !!!
 * WE ARE BUILDING FOR Java 8, do not use any Java 9+ features
 */
public class ItemTextProviderPre_1_17 extends Provider<AbstractItemTextPre_1_17> {
    @Override
    protected @NotNull AbstractItemTextPre_1_17 provide(int ver) {
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        if (ver == f("1.8")) {
            return new ItemText_1_8_R1();
        }else if (ver <= f("1.8.3")) {
            return new ItemText_1_8_R2();
        }else if (ver <= f("1.8.8")) {
            return new ItemText_1_8_R3();
        }else if (ver <= f("1.9.2")) {
            return new ItemText_1_9_R1();
        }else if (ver <= f("1.9.4")) {
            return new ItemText_1_9_R2();
        }else if (ver <= f("1.10.2")) {
            return new ItemText_1_10_R1();
        }else if (ver <= f("1.11.2")) {
            return new ItemText_1_11_R1();
        }else if (ver <= f("1.12.2")) {
            return new ItemText_1_12_R1();
        }else if (ver <= f("1.13")) {
            return new ItemText_1_13_R1();
        }else if (ver <= f("1.13.2")) {
            return new ItemText_1_13_R2();
        }else if (ver <= f("1.14.4")) {
            return new ItemText_1_14_R1();
        }else if (ver <= f("1.15.2")) {
            return new ItemText_1_15_R1();
        }else if (ver <= f("1.16.1")) {
            return new ItemText_1_16_R1();
        }else if (ver <= f("1.16.3")) {
            return new ItemText_1_16_R2();
        }else if (ver <= f("1.16.5")) {
            return new ItemText_1_16_R3();
        }

        throw new IllegalArgumentException("Version not supported (>=? 1.17): " + ver);
    }
}
