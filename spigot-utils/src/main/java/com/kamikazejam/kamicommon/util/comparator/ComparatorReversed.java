package com.kamikazejam.kamicommon.util.comparator;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class ComparatorReversed<T> extends ComparatorAbstractWrapper<T, T> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	@Contract("_ -> new")
	public static <T> @NotNull ComparatorReversed<T> get(Comparator<T> comparator) {
		return new ComparatorReversed<>(comparator);
	}

	public ComparatorReversed(Comparator<T> comparator) {
		super(comparator);
		this.setReversed(true);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public int compareInner(T type1, T type2) {
		return -this.getComparator().compare(type1, type2);
	}

}
