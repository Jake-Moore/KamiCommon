package com.kamikazejam.kamicommon.command.impl;

import com.kamikazejam.kamicommon.command.CommandContext;
import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.util.VersionControl;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.jetbrains.annotations.NotNull;

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
		this.setDesc("Gets the version of this plugin.");
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	@Override
	public void perform(@NotNull CommandContext context) throws KamiCommonException {
		VersionControl.sendDetails(getPlugin(), context.getSender());
	}
}
