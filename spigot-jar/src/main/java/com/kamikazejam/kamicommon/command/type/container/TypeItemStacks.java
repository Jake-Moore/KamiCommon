package com.kamikazejam.kamicommon.command.type.container;

import com.kamikazejam.kamicommon.command.editor.CommandEditAbstract;
import com.kamikazejam.kamicommon.command.editor.CommandEditItemStacks;
import com.kamikazejam.kamicommon.command.editor.EditSettings;
import com.kamikazejam.kamicommon.command.editor.Property;
import com.kamikazejam.kamicommon.command.type.TypeItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TypeItemStacks extends TypeList<ItemStack> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final TypeItemStacks i = new TypeItemStacks();

	public static TypeItemStacks get() {
		return i;
	}

	public TypeItemStacks() {
		super(TypeItemStack.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public <O> CommandEditAbstract<O, List<ItemStack>> createEditCommand(EditSettings<O> settings, Property<O, List<ItemStack>> property) {
		return new CommandEditItemStacks<>(settings, property);
	}

}
