package com.kamikazejam.kamicommon.command.editor;

import com.kamikazejam.kamicommon.command.type.primitive.TypeInteger;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandEditContainerInsert<O, V> extends CommandEditContainerAbstract<O, V> {
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CommandEditContainerInsert(@NotNull EditSettings<O> settings, @NotNull Property<O, V> property) {
		// Super	
		super(settings, property);

		// Parameters
		this.addParameter(TypeInteger.get(), "index");
		this.addParametersElement(true);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void alterElements(@NotNull List<Object> elements) throws KamiCommonException {
		// Args
		int index = this.readArg();
		Object element = this.readElement();

		// Alter
		elements.add(index, element);
	}

}
