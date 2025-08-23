package com.kamikazejam.kamicommon.command.requirement;

import com.kamikazejam.kamicommon.command.KamiCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class RequirementIsntPlayer extends RequirementAbstract {

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final RequirementIsntPlayer i = new RequirementIsntPlayer();

	public static RequirementIsntPlayer get() {
		return i;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public boolean apply(CommandSender sender, KamiCommand command) {
		return !(sender instanceof Player);
	}

	@Override
	public String createErrorMessage(CommandSender sender, KamiCommand command) {
		return KamiCommand.Config.getSenderMustNotBePlayer();
	}

}
