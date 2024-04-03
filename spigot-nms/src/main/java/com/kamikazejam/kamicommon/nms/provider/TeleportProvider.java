package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.abstraction.teleport.AbstractTeleporter;
import com.kamikazejam.kamicommon.nms.teleport.*;
import org.jetbrains.annotations.NotNull;

/**
 * !!! Gradle Compatability Requires this module to be set to Java16 !!!
 * WE ARE BUILDING FOR Java 8, do not use any Java 9+ features
 */
@SuppressWarnings("EnhancedSwitchMigration")
public class TeleportProvider extends Provider<AbstractTeleporter> {
    @Override
    protected @NotNull AbstractTeleporter provide(double ignored, String nmsVersion) {
        //TODO: Add new versions as they come out
        switch(nmsVersion) {
            case "v1_8_R1":
                return new Teleporter1_8_R1();
            case "v1_8_R2":
                return new Teleporter1_8_R2();
            case "v1_8_R3":
                return new Teleporter1_8_R3();
            case "v1_9_R1":
                return new Teleporter1_9_R1();
            case "v1_9_R2":
                return new Teleporter1_9_R2();
            case "v1_10_R1":
                return new Teleporter1_10_R1();
            case "v1_11_R1":
                return new Teleporter1_11_R1();
            case "v1_12_R1":
                return new Teleporter1_12_R1();
            case "v1_13_R1":
                return new Teleporter1_13_R1();
            case "v1_13_R2":
                return new Teleporter1_13_R2();
            case "v1_14_R1":
                return new Teleporter1_14_R1();
            case "v1_15_R1":
                return new Teleporter1_15_R1();
            case "v1_16_R1":
                return new Teleporter1_16_R1();
            case "v1_16_R2":
                return new Teleporter1_16_R2();
            case "v1_16_R3":
                return new Teleporter1_16_R3();
            case "v1_17_R1":
                return new Teleporter1_17_R1();
            case "v1_18_R1":
                return new Teleporter1_18_R1();
            case "v1_18_R2":
                return new Teleporter1_18_R2();
            case "v1_19_R1":
                return new Teleporter1_19_R1();
            case "v1_19_R2":
                return new Teleporter1_19_R2();
            case "v1_19_R3":
                return new Teleporter1_19_R3();
            case "v1_20_R1":
                return new Teleporter1_20_R1();
            case "v1_20_R2":
                return new Teleporter1_20_R2();
            case "v1_20_R3":
                return new Teleporter1_20_R3();
            default:
                throw new IllegalArgumentException(nmsVersion + " isn't a supported version!");
        }
    }
}
