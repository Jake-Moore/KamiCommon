package com.kamikazejam.kamicommon.util.comparator;

import org.jetbrains.annotations.Contract;

import java.util.Objects;

public class ComparatorHashCode extends ComparatorAbstract<Object> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final ComparatorHashCode i = new ComparatorHashCode();

	@Contract(pure = true)
	public static ComparatorHashCode get() {
		return i;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public int compareInner(Object object1, Object object2) {
		return Integer.compare(Objects.hashCode(object1), Objects.hashCode(object2));
	}

}
