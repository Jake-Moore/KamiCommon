package com.kamikazejam.kamicommon.util.comparator;

import org.jetbrains.annotations.Contract;

public class ComparatorIdentity extends ComparatorAbstract<Object> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final ComparatorIdentity i = new ComparatorIdentity();

	@Contract(pure = true)
	public static ComparatorIdentity get() {
		return i;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public int compareInner(Object object1, Object object2) {
		if (object1 == object2) return 0;
		return Integer.compare(System.identityHashCode(object1), System.identityHashCode(object2));
	}

}
