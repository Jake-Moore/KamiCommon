package com.kamikazejam.kamicommon.util.teleport.ps;

import com.kamikazejam.kamicommon.util.StringUtil;

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
				StringUtil.t("&7&oNULL"),
				StringUtil.t(""),
				StringUtil.t("&b" + PS.NAME_SERIALIZED_WORLD + " &d%s"),
				StringUtil.t("&b" + PS.NAME_SERIALIZED_BLOCKX + " &d%d"),
				StringUtil.t("&b" + PS.NAME_SERIALIZED_BLOCKY + " &d%d"),
				StringUtil.t("&b" + PS.NAME_SERIALIZED_BLOCKZ + " &d%d"),
				StringUtil.t("&b" + PS.NAME_SERIALIZED_LOCATIONX + " &d%.2f"),
				StringUtil.t("&b" + PS.NAME_SERIALIZED_LOCATIONY + " &d%.2f"),
				StringUtil.t("&b" + PS.NAME_SERIALIZED_LOCATIONZ + " &d%.2f"),
				StringUtil.t("&b" + PS.NAME_SERIALIZED_CHUNKX + " &d%d"),
				StringUtil.t("&b" + PS.NAME_SERIALIZED_CHUNKZ + " &d%d"),
				StringUtil.t("&b" + PS.NAME_SERIALIZED_PITCH + " &d%.2f"),
				StringUtil.t("&b" + PS.NAME_SERIALIZED_YAW + " &d%.2f"),
				StringUtil.t("&b" + PS.NAME_SERIALIZED_VELOCITYX + " &d%.2f"),
				StringUtil.t("&b" + PS.NAME_SERIALIZED_VELOCITYY + " &d%.2f"),
				StringUtil.t("&b" + PS.NAME_SERIALIZED_VELOCITYZ + " &d%.2f"),
				StringUtil.t(" "),
				StringUtil.t("")
		);
	}
}
