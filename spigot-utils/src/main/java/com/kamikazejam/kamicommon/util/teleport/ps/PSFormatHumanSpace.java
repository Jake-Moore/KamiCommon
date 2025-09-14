package com.kamikazejam.kamicommon.util.teleport.ps;

import com.kamikazejam.kamicommon.util.LegacyColors;

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
                LegacyColors.t("&7&oNULL"),
                LegacyColors.t(""),
                LegacyColors.t("&d%s"),
                LegacyColors.t("&d%d"),
                LegacyColors.t("&d%d"),
                LegacyColors.t("&d%d"),
                LegacyColors.t("&d%.2f"),
                LegacyColors.t("&d%.2f"),
                LegacyColors.t("&d%.2f"),
                LegacyColors.t("&d%d"),
                LegacyColors.t("&d%d"),
                LegacyColors.t("&d%.2f"),
                LegacyColors.t("&d%.2f"),
                LegacyColors.t("&d%.2f"),
                LegacyColors.t("&d%.2f"),
                LegacyColors.t("&d%.2f"),
                LegacyColors.t(" "),
                LegacyColors.t("")
        );
    }
}
