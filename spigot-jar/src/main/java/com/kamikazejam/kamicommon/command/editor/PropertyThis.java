package com.kamikazejam.kamicommon.command.editor;

import com.kamikazejam.kamicommon.command.type.Type;
import org.jetbrains.annotations.NotNull;

public class PropertyThis<O> extends Property<O, O> {
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public PropertyThis(@NotNull Type<O> objectType) {
		super(objectType, objectType, objectType.getName());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public O getRaw(O object) {
		return object;
	}

	@Override
	public O setRaw(O object, O value) {
		return object;
	}

}
