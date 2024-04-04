package com.kamikazejam.kamicommon.command.editor;

import com.kamikazejam.kamicommon.command.requirement.RequirementEditorPropertyCreated;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class CommandEditItemStacksAbstract<O> extends CommandEditAbstract<O, List<ItemStack>> {
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CommandEditItemStacksAbstract(@NotNull EditSettings<O> settings, @NotNull Property<O, List<ItemStack>> property) {
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

}
