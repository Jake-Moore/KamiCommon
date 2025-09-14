package com.kamikazejam.kamicommon.util.teleport.ps;

import com.kamikazejam.kamicommon.nms.NmsAPI;

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
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<gray><italic>NULL"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage(""),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<aqua>" + PS.NAME_SERIALIZED_WORLD + " <light_purple>%s"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<aqua>" + PS.NAME_SERIALIZED_BLOCKX + " <light_purple>%d"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<aqua>" + PS.NAME_SERIALIZED_BLOCKY + " <light_purple>%d"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<aqua>" + PS.NAME_SERIALIZED_BLOCKZ + " <light_purple>%d"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<aqua>" + PS.NAME_SERIALIZED_LOCATIONX + " <light_purple>%.2f"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<aqua>" + PS.NAME_SERIALIZED_LOCATIONY + " <light_purple>%.2f"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<aqua>" + PS.NAME_SERIALIZED_LOCATIONZ + " <light_purple>%.2f"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<aqua>" + PS.NAME_SERIALIZED_CHUNKX + " <light_purple>%d"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<aqua>" + PS.NAME_SERIALIZED_CHUNKZ + " <light_purple>%d"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<aqua>" + PS.NAME_SERIALIZED_PITCH + " <light_purple>%.2f"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<aqua>" + PS.NAME_SERIALIZED_YAW + " <light_purple>%.2f"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<aqua>" + PS.NAME_SERIALIZED_VELOCITYX + " <light_purple>%.2f"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<aqua>" + PS.NAME_SERIALIZED_VELOCITYY + " <light_purple>%.2f"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("<aqua>" + PS.NAME_SERIALIZED_VELOCITYZ + " <light_purple>%.2f"),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage(" "),
                NmsAPI.getVersionedComponentSerializer().fromMiniMessage("")
        );
	}
}
