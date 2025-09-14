package com.kamikazejam.kamicommon.util.teleport.ps;

import com.kamikazejam.kamicommon.nms.NmsAPI;

public class PSFormatFormal extends PSFormatAbstract {
    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //

    private static final PSFormatFormal i = new PSFormatFormal();

    public static PSFormatFormal get() {
        return i;
    }

    private PSFormatFormal() {
        super(
                NmsAPI.getVersionedComponentSerializer().fromPlainText("PS{NULL}"),
                NmsAPI.getVersionedComponentSerializer().fromPlainText("PS{"),
                NmsAPI.getVersionedComponentSerializer().fromPlainText(PS.NAME_SERIALIZED_WORLD + ": %s"),
                NmsAPI.getVersionedComponentSerializer().fromPlainText(PS.NAME_SERIALIZED_BLOCKX + ": %d"),
                NmsAPI.getVersionedComponentSerializer().fromPlainText(PS.NAME_SERIALIZED_BLOCKY + ": %d"),
                NmsAPI.getVersionedComponentSerializer().fromPlainText(PS.NAME_SERIALIZED_BLOCKZ + ": %d"),
                NmsAPI.getVersionedComponentSerializer().fromPlainText(PS.NAME_SERIALIZED_LOCATIONX + ": %.2f"),
                NmsAPI.getVersionedComponentSerializer().fromPlainText(PS.NAME_SERIALIZED_LOCATIONY + ": %.2f"),
                NmsAPI.getVersionedComponentSerializer().fromPlainText(PS.NAME_SERIALIZED_LOCATIONZ + ": %.2f"),
                NmsAPI.getVersionedComponentSerializer().fromPlainText(PS.NAME_SERIALIZED_CHUNKX + ": %d"),
                NmsAPI.getVersionedComponentSerializer().fromPlainText(PS.NAME_SERIALIZED_CHUNKZ + ": %d"),
                NmsAPI.getVersionedComponentSerializer().fromPlainText(PS.NAME_SERIALIZED_PITCH + ": %.2f"),
                NmsAPI.getVersionedComponentSerializer().fromPlainText(PS.NAME_SERIALIZED_YAW + ": %.2f"),
                NmsAPI.getVersionedComponentSerializer().fromPlainText(PS.NAME_SERIALIZED_VELOCITYX + ": %.2f"),
                NmsAPI.getVersionedComponentSerializer().fromPlainText(PS.NAME_SERIALIZED_VELOCITYY + ": %.2f"),
                NmsAPI.getVersionedComponentSerializer().fromPlainText(PS.NAME_SERIALIZED_VELOCITYZ + ": %.2f"),
                NmsAPI.getVersionedComponentSerializer().fromPlainText(", "),
                NmsAPI.getVersionedComponentSerializer().fromPlainText("}")
        );
    }
}
