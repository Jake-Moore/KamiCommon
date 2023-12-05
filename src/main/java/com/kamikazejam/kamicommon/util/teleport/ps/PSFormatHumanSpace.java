package com.kamikazejam.kamicommon.util.teleport.ps;

import com.kamikazejam.kamicommon.util.Txt;

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
                Txt.parse("<silver><em>NULL"),
                Txt.parse(""),
                Txt.parse("<v>%s"),
                Txt.parse("<v>%d"),
                Txt.parse("<v>%d"),
                Txt.parse("<v>%d"),
                Txt.parse("<v>%.2f"),
                Txt.parse("<v>%.2f"),
                Txt.parse("<v>%.2f"),
                Txt.parse("<v>%d"),
                Txt.parse("<v>%d"),
                Txt.parse("<v>%.2f"),
                Txt.parse("<v>%.2f"),
                Txt.parse("<v>%.2f"),
                Txt.parse("<v>%.2f"),
                Txt.parse("<v>%.2f"),
                Txt.parse(" "),
                Txt.parse("")
        );
    }
}
