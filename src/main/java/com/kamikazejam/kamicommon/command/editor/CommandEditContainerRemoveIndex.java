package com.kamikazejam.kamicommon.command.editor;

import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import com.kamikazejam.kamicommon.command.type.primitive.TypeInteger;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandEditContainerRemoveIndex<O, V> extends CommandEditContainerAbstract<O, V> {
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CommandEditContainerRemoveIndex(@NotNull EditSettings<O> settings, @NotNull Property<O, V> property) {
		// Super	
		super(settings, property);

		// Parameters
		this.addParameter(TypeInteger.get(), "index");
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void alterElements(@NotNull List<Object> elements) throws KamiCommonException {
		// Args
		int index = this.readArg();

		// Alter
		elements.remove(index);
	}

}
