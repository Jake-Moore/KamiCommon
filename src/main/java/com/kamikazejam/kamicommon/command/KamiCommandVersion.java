package com.kamikazejam.kamicommon.command;

import com.kamikazejam.kamicommon.VersionControl;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;

@SuppressWarnings("unused")
public class KamiCommandVersion extends KamiCommand {
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	public KamiCommandVersion() {
		// Aliases
		this.addAliases("ver", "version");

		this.addRequirements(RequirementHasPerm.get("kamicommon.command.version"));

		// Other
		this.setDesc("");
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	@Override
	public void perform() throws KamiCommonException {
		VersionControl.sendDetails(getPlugin(), sender);
	}
}
