package com.kamikazejam.kamicommon.command.editor;

import com.kamikazejam.kamicommon.command.type.primitive.TypeInteger;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandEditContainerMove<O, V> extends CommandEditContainerAbstract<O, V> {
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CommandEditContainerMove(@NotNull EditSettings<O> settings, @NotNull Property<O, V> property) {
		// Super	
		super(settings, property);

		// Parameters
		this.addParameter(TypeInteger.get(), "indexFrom");
		this.addParameter(TypeInteger.get(), "indexTo");
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void alterElements(@NotNull List<Object> elements) throws KamiCommonException {
		// Args
		int indexFrom = this.readArg();
		int indexTo = this.readArg();

		// Alter
		Object element = elements.remove(indexFrom);
		elements.add(indexTo, element);
	}

}
