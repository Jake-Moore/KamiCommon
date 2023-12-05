package com.kamikazejam.kamicommon.command.editor;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandEditItemStacks<O> extends CommandEditAbstract<O, List<ItemStack>> {
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CommandEditItemStacks(@NotNull EditSettings<O> settings, @NotNull Property<O, List<ItemStack>> property) {
		// Super
		super(settings, property, true);

		// Children
		this.addChild(new CommandEditShow<>(settings, property));

		if (property.isNullable()) {
			this.addChild(new CommandEditCreate<>(settings, property));
			this.addChild(new CommandEditDelete<>(settings, property));
		}

		if (property.isEditable()) {
			this.addChild(new CommandEditItemStacksOpen<>(settings, property));
		}
	}

}
