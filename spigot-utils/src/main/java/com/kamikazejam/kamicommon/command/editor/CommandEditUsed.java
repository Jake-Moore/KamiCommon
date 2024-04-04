package com.kamikazejam.kamicommon.command.editor;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandEditUsed<O> extends CommandEditSimple<CommandSender, O> {
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CommandEditUsed(@NotNull EditSettings<O> settings) {
		// Super
		super(settings.getUsedSettings(), settings.getUsedProperty());

		// Aliases
		this.setAliases("used", "selected");

		// Desc
		this.setDesc("edit used " + this.getProperty().getValueType().getName());
	}

}
