package com.kamikazejam.kamicommon.command.requirement;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.command.Lang;
import com.kamikazejam.kamicommon.util.Txt;
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
		return Txt.parse(Lang.COMMAND_SENDER_MUST_NOT_BE_PLAYER);
	}

}
