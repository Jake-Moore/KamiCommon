package com.kamikazejam.kamicommon.command.editor;

import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.command.requirement.RequirementEditorPropertyCreated;
import org.jetbrains.annotations.NotNull;

public class CommandEditDelete<O, V> extends CommandEditAbstract<O, V> {
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CommandEditDelete(@NotNull EditSettings<O> settings, @NotNull Property<O, V> property) {
		// Super	
		super(settings, property, true);

		// Aliases
		String alias = this.createCommandAlias();
		this.setAliases(alias);

		// Desc
		this.setDesc(alias + " " + this.getPropertyName());

		// Requirements
		this.addRequirements(RequirementEditorPropertyCreated.get(true));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void perform() throws KamiCommonException {
		this.attemptSet(null);
	}

}
