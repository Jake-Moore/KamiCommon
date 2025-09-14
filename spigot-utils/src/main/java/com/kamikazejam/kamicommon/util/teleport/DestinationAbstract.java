package com.kamikazejam.kamicommon.util.teleport;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.util.teleport.ps.PS;
import com.kamikazejam.kamicommon.util.teleport.ps.PSFormatHumanSpace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class DestinationAbstract implements Destination {

	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	protected @Nullable VersionedComponent desc = null;

	// -------------------------------------------- //
	// ABSTRACT
	// -------------------------------------------- //

	public @Nullable PS getPsInner() {
		return null;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public PS getPs(Object watcherObject) throws KamiCommonException {
		PS ret = this.getPsInner();
		if (ret == null) {
			throw new KamiCommonException().addMsg(this.getMessagePsNull(watcherObject));
		}
		return ret;
	}

	@Override
	public boolean hasPs() {
		try {
			return this.getPs(null) != null;
		} catch (KamiCommonException e) {
			return false;
		}
	}

	@Override
	public @NotNull VersionedComponent getMessagePsNull(Object watcherObject) {
        String miniMessageDesc = this.getDesc(watcherObject).serializeMiniMessage();
		String miniMessage = String.format(
                "<red>Location for <light_purple>%s<red> could not be found.",
                miniMessageDesc
        );
        return NmsAPI.getVersionedComponentSerializer().fromMiniMessage(miniMessage);
	}

	@Override
	public @NotNull VersionedComponent getDesc(Object watcherObject) {
		if (this.desc != null) return this.desc;
		try {
			PS ps = this.getPs(watcherObject);
			return PSFormatHumanSpace.get().format(ps);
		} catch (KamiCommonException e) {
			return NmsAPI.getVersionedComponentSerializer().fromPlainText("null");
		}
	}

	@Override
	public void setDesc(@NotNull VersionedComponent desc) {
		this.desc = desc;
	}

}
