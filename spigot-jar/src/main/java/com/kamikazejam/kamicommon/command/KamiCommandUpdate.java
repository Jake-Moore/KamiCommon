package com.kamikazejam.kamicommon.command;

import com.kamikazejam.kamicommon.autoupdate.AutoUpdate;
import com.kamikazejam.kamicommon.command.requirement.RequirementHasPerm;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class KamiCommandUpdate extends KamiCommand {
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	public KamiCommandUpdate() {
		// Aliases
		this.addAliases("update");

		this.addRequirements(RequirementHasPerm.get("kamicommon.command.update"));

		// Other
		this.setDesc("Runs the update check again, downloading new jars if applicable.");
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	@Override
	public void perform() throws KamiCommonException {
		AutoUpdate.updateNow((JavaPlugin) getPlugin());
	}
}
