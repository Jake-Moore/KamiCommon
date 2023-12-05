package com.kamikazejam.kamicommon.util.comparator;

import org.jetbrains.annotations.Contract;

public class ComparatorCaseInsensitive extends ComparatorAbstract<String> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final ComparatorCaseInsensitive i = new ComparatorCaseInsensitive();

	@Contract(pure = true)
	public static ComparatorCaseInsensitive get() {
		return i;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public int compareInner(String string1, String string2) {
		return String.CASE_INSENSITIVE_ORDER.compare(string1, string2);
	}

}
