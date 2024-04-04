package com.kamikazejam.kamicommon.command.editor;

import com.kamikazejam.kamicommon.command.type.primitive.TypeInteger;
import com.kamikazejam.kamicommon.util.exception.KamiCommonException;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class CommandEditContainerSwap<O, V> extends CommandEditContainerAbstract<O, V> {
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CommandEditContainerSwap(@NotNull EditSettings<O> settings, @NotNull Property<O, V> property) {
		// Super	
		super(settings, property);

		// Parameters
		this.addParameter(TypeInteger.get(), "indexOne");
		this.addParameter(TypeInteger.get(), "indexTwo");
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void alterElements(List<Object> elements) throws KamiCommonException {
		// Args
		int indexOne = this.readArg();
		int indexTwo = this.readArg();

		// Alter
		Collections.swap(elements, indexOne, indexTwo);
	}

}
