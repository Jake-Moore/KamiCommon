package com.kamikazejam.kamicommon.command.editor;

import com.kamikazejam.kamicommon.command.Parameter;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.jetbrains.annotations.NotNull;

public class CommandEditShow<O, V> extends CommandEditAbstract<O, V> {
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CommandEditShow(@NotNull EditSettings<O> settings, @NotNull Property<O, V> property) {
		// Super	
		super(settings, property, false);

		// Aliases
		String alias = this.createCommandAlias();
		this.setAliases(alias);

		// Parameters
		this.addParameter(Parameter.getPage());

		// Desc
		this.setDesc(alias + " " + this.getPropertyName());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void perform() throws KamiCommonException {
		int page = this.readArg();
		this.show(page);
	}

}
