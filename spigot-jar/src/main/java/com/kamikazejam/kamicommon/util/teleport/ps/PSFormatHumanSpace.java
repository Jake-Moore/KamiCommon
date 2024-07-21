package com.kamikazejam.kamicommon.util.teleport.ps;

import com.kamikazejam.kamicommon.util.StringUtil;

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
                StringUtil.t("&7&oNULL"),
                StringUtil.t(""),
                StringUtil.t("&d%s"),
                StringUtil.t("&d%d"),
                StringUtil.t("&d%d"),
                StringUtil.t("&d%d"),
                StringUtil.t("&d%.2f"),
                StringUtil.t("&d%.2f"),
                StringUtil.t("&d%.2f"),
                StringUtil.t("&d%d"),
                StringUtil.t("&d%d"),
                StringUtil.t("&d%.2f"),
                StringUtil.t("&d%.2f"),
                StringUtil.t("&d%.2f"),
                StringUtil.t("&d%.2f"),
                StringUtil.t("&d%.2f"),
                StringUtil.t(" "),
                StringUtil.t("")
        );
    }
}
