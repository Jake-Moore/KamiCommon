package com.kamikazejam.kamicommon.command.requirement;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.util.mson.MsonMessenger;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

public abstract class RequirementAbstract implements Requirement, Serializable {

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public boolean apply(CommandSender sender) {
		return this.apply(sender, null);
	}

	@Override
	public String createErrorMessage(CommandSender sender) {
		return this.createErrorMessage(sender, null);
	}

	@Contract("null -> !null")
	public static String getDesc(KamiCommand command) {
		if (command == null) return "do that";
		return command.getDesc();
	}

	// -------------------------------------------- //
	// BULK
	// -------------------------------------------- //

	public static boolean isRequirementsMet(@NotNull Iterable<@NotNull Requirement> requirements, CommandSender sender, KamiCommand command, boolean verbose) {
		String error = getRequirementsError(requirements, sender, command, verbose);
		if (error != null && verbose) MsonMessenger.get().messageOne(sender, error);
		return error == null;
	}

	public static @Nullable String getRequirementsError(@NotNull Iterable<@NotNull Requirement> requirements, CommandSender sender, KamiCommand command, boolean verbose) {
		for (Requirement requirement : requirements) {
			if (requirement.apply(sender, command)) continue;
			if (!verbose) return "";
			return requirement.createErrorMessage(sender, command);
		}
		return null;
	}

}
