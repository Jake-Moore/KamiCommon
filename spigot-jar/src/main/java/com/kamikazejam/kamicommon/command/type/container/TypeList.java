package com.kamikazejam.kamicommon.command.type.container;

import com.kamikazejam.kamicommon.command.type.Type;
import com.kamikazejam.kamicommon.util.collections.KamiList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TypeList<E> extends TypeContainer<List<E>, E> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	@Contract("_ -> new")
	public static <E> @NotNull TypeList<E> get(Type<E> innerType) {
		return new TypeList<>(innerType);
	}

	public TypeList(Type<E> innerType) {
		super(List.class, innerType);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public List<E> createNewInstance() {
		return new KamiList<>();
	}

}
