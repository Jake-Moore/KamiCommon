package com.kamikazejam.kamicommon.util.teleport.ps;

import com.kamikazejam.kamicommon.nms.NmsAPI;

public class PSFormatHumanSpace extends PSFormatAbstract {
    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //

    private static final PSFormatHumanSpace i = new PSFormatHumanSpace();

    public static PSFormatHumanSpace get() {
        return i;
    }

    private PSFormatHumanSpace() {
        super(
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<gray><italic>NULL"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage(""),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<light_purple>%s"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<light_purple>%d"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<light_purple>%d"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<light_purple>%d"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<light_purple>%.2f"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<light_purple>%.2f"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<light_purple>%.2f"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<light_purple>%d"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<light_purple>%d"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<light_purple>%.2f"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<light_purple>%.2f"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<light_purple>%.2f"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<light_purple>%.2f"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<light_purple>%.2f"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage(" "),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("")
        );
    }
}
