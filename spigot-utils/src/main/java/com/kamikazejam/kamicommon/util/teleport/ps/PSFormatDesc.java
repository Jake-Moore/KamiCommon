package com.kamikazejam.kamicommon.util.teleport.ps;

import com.kamikazejam.kamicommon.util.LegacyColors;

@SuppressWarnings("unused")
public class PSFormatDesc extends PSFormatAbstract {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final PSFormatDesc i = new PSFormatDesc();

	public static PSFormatDesc get() {
		return i;
	}

	private PSFormatDesc() {
		super(
				LegacyColors.t("&7&oNULL"),
				LegacyColors.t(""),
				LegacyColors.t("&b" + PS.NAME_SERIALIZED_WORLD + " &d%s"),
				LegacyColors.t("&b" + PS.NAME_SERIALIZED_BLOCKX + " &d%d"),
				LegacyColors.t("&b" + PS.NAME_SERIALIZED_BLOCKY + " &d%d"),
				LegacyColors.t("&b" + PS.NAME_SERIALIZED_BLOCKZ + " &d%d"),
				LegacyColors.t("&b" + PS.NAME_SERIALIZED_LOCATIONX + " &d%.2f"),
				LegacyColors.t("&b" + PS.NAME_SERIALIZED_LOCATIONY + " &d%.2f"),
				LegacyColors.t("&b" + PS.NAME_SERIALIZED_LOCATIONZ + " &d%.2f"),
				LegacyColors.t("&b" + PS.NAME_SERIALIZED_CHUNKX + " &d%d"),
				LegacyColors.t("&b" + PS.NAME_SERIALIZED_CHUNKZ + " &d%d"),
				LegacyColors.t("&b" + PS.NAME_SERIALIZED_PITCH + " &d%.2f"),
				LegacyColors.t("&b" + PS.NAME_SERIALIZED_YAW + " &d%.2f"),
				LegacyColors.t("&b" + PS.NAME_SERIALIZED_VELOCITYX + " &d%.2f"),
				LegacyColors.t("&b" + PS.NAME_SERIALIZED_VELOCITYY + " &d%.2f"),
				LegacyColors.t("&b" + PS.NAME_SERIALIZED_VELOCITYZ + " &d%.2f"),
				LegacyColors.t(" "),
				LegacyColors.t("")
		);
	}
}
