package com.kamikazejam.kamicommon.command.requirement;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
	public @NotNull VersionedComponent createErrorMessage(CommandSender sender, KamiCommand command) {
        return NmsAPI.getVersionedComponentSerializer().fromMiniMessage(KamiCommand.Config.getSenderMustBePlayerMini());
	}

}
