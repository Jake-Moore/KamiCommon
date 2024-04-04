package com.kamikazejam.kamicommon.command.requirement;

import com.kamikazejam.kamicommon.command.KamiCommand;
import com.kamikazejam.kamicommon.util.predicate.Predicate;
import org.bukkit.command.CommandSender;

public interface Requirement extends Predicate<CommandSender> {
	boolean apply(CommandSender sender, KamiCommand command);

	// This just composes the error message and does NOT test the requirement at all.

	String createErrorMessage(CommandSender sender);

	String createErrorMessage(CommandSender sender, KamiCommand command);
}
