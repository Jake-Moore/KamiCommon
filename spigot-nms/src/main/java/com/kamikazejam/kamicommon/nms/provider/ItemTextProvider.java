package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.abstraction.hoveritem.AbstractItemText;
import com.kamikazejam.kamicommon.nms.hoveritem.*;
import org.jetbrains.annotations.NotNull;

/**
 * !!! Gradle Compatability Requires this module to be set to Java16 !!!
 * WE ARE BUILDING FOR Java 8, do not use any Java 9+ features
 */
@SuppressWarnings("EnhancedSwitchMigration")
public class ItemTextProvider extends Provider<AbstractItemText> {
    @Override
    protected @NotNull AbstractItemText provide(double ignored, String nmsVersion) {
        //TODO: Add new versions as they come out
        switch(nmsVersion) {
            case "v1_8_R1":
                return new ItemText_1_8_R1();
            case "v1_8_R2":
                return new ItemText_1_8_R2();
            case "v1_8_R3":
                return new ItemText_1_8_R3();
            case "v1_9_R1":
                return new ItemText_1_9_R1();
            case "v1_9_R2":
                return new ItemText_1_9_R2();
            case "v1_10_R1":
                return new ItemText_1_10_R1();
            case "v1_11_R1":
                return new ItemText_1_11_R1();
            case "v1_12_R1":
                return new ItemText_1_12_R1();
            case "v1_13_R1":
                return new ItemText_1_13_R1();
            case "v1_13_R2":
                return new ItemText_1_13_R2();
            case "v1_14_R1":
                return new ItemText_1_14_R1();
            case "v1_15_R1":
                return new ItemText_1_15_R1();
            case "v1_16_R1":
                return new ItemText_1_16_R1();
            case "v1_16_R2":
                return new ItemText_1_16_R2();
            case "v1_16_R3":
                return new ItemText_1_16_R3();
            case "v1_17_R1":
                return new ItemText_1_17_R1();
            case "v1_18_R1":
                return new ItemText_1_18_R1();
            case "v1_18_R2":
                return new ItemText_1_18_R2();
            case "v1_19_R1":
                return new ItemText_1_19_R1();
            case "v1_19_R2":
                return new ItemText_1_19_R2();
            case "v1_19_R3":
                return new ItemText_1_19_R3();
            case "v1_20_R1":
                return new ItemText_1_20_R1();
            case "v1_20_R2":
                return new ItemText_1_20_R2();
            case "v1_20_R3":
                return new ItemText_1_20_R3();
            default:
                throw new IllegalArgumentException(nmsVersion + " isn't a supported version!");
        }
    }
}
