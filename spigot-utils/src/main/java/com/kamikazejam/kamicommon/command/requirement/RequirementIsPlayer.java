package com.kamikazejam.kamicommon.command.requirement;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RequirementIsPlayer extends RequirementAbstract {

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final RequirementIsPlayer i = new RequirementIsPlayer();

	public static RequirementIsPlayer get() {
		return i;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public boolean apply(CommandSender sender, KamiCommand command) {
		return sender instanceof Player;
	}

	@Override
	public String createErrorMessage(CommandSender sender, KamiCommand command) {
		return Lang.COMMAND_SENDER_MUST_BE_PLAYER;
	}

}
