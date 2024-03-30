package com.kamikazejam.kamicommon.util.comparator;

import com.kamikazejam.kamicommon.util.interfaces.Named;
import org.jetbrains.annotations.Contract;

public class ComparatorNamed extends ComparatorAbstract<Object> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final ComparatorNamed i = new ComparatorNamed();

	@Contract(pure = true)
	public static ComparatorNamed get() {
		return i;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public int compareInner(Object object1, Object object2) {
		// Create
		int ret;

		// Instance Of
		Named named1 = null;
		Named named2 = null;
		if (object1 instanceof Named) named1 = (Named) object1;
		if (object2 instanceof Named) named2 = (Named) object2;
		ret = ComparatorNull.get().compare(named1, named2);
		if (ret != 0) return ret;
		if (named1 == null && named2 == null) return ret;

		// Name
		String name1 = named1.getName();
		String name2 = named2.getName();
		ret = ComparatorNaturalOrder.get().compare(name1, name2);

		// Return
		return ret;
	}

}
