package com.kamikazejam.kamicommon.util.predicate;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

// Inspired by: String#regionMatches(ignoreCase, toffset, other, ooffset, len)
@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class PredicateEqualsIgnoreCase implements Predicate<String> {
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	private final String strLower;
	private final String strUpper;

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	@Contract("_ -> new")
	public static @NotNull PredicateEqualsIgnoreCase get(@NotNull String prefix) {
		return new PredicateEqualsIgnoreCase(prefix);
	}

	@Contract("null -> fail")
	public PredicateEqualsIgnoreCase(String str) {
		if (str == null) throw new NullPointerException("str");
		this.strLower = str.toLowerCase();
		this.strUpper = str.toUpperCase();
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public boolean apply(String str) {
		if (str == null) return false;
		int index = this.strLower.length();
		if (str.length() != index) return false;
		while (index-- > 0) {
			char c = str.charAt(index);
			if (c == strLower.charAt(index)) continue;
			if (c != strUpper.charAt(index)) return false;
		}
		return true;
	}

}
