package com.kamikazejam.kamicommon.util.teleport;

import com.kamikazejam.kamicommon.util.LegacyColors;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.util.teleport.ps.PS;
import com.kamikazejam.kamicommon.util.teleport.ps.PSFormatHumanSpace;

public abstract class DestinationAbstract implements Destination {

	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	protected String desc = null;

	// -------------------------------------------- //
	// ABSTRACT
	// -------------------------------------------- //

	public PS getPsInner() {
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
	public String getMessagePsNull(Object watcherObject) {
		String desc = this.getDesc(watcherObject);
		String f = String.format("&cLocation for &d%s&c could not be found.", desc);
		return LegacyColors.t(f);
	}

	@Override
	public String getDesc(Object watcherObject) {
		if (this.desc != null) return this.desc;
		try {
			PS ps = this.getPs(watcherObject);
			return PSFormatHumanSpace.get().format(ps);
		} catch (KamiCommonException e) {
			return "null";
		}
	}

	@Override
	public void setDesc(String desc) {
		this.desc = desc;
	}

}
