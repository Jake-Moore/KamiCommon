package com.kamikazejam.kamicommon.util.predicate;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

// Inspired by: String#regionMatches(ignoreCase, toffset, other, ooffset, len)
@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class PredicateStartsWithIgnoreCase implements Predicate<String> {
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	private final @NotNull String prefixLower;
	private final @NotNull String prefixUpper;

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	@Contract("_ -> new")
	public static @NotNull PredicateStartsWithIgnoreCase get(@NotNull String prefix) {
		return new PredicateStartsWithIgnoreCase(prefix);
	}

	@Contract("null -> fail")
	public PredicateStartsWithIgnoreCase(String prefix) {
		if (prefix == null) throw new NullPointerException("prefix");
		this.prefixLower = prefix.toLowerCase();
		this.prefixUpper = prefix.toUpperCase();
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public boolean apply(String str) {
		if (str == null) return false;
		int index = this.prefixLower.length();
		if (str.length() < index) return false;
		while (index-- > 0) {
			char c = str.charAt(index);
			if (c == prefixLower.charAt(index)) continue;
			if (c != prefixUpper.charAt(index)) return false;
		}
		return true;
	}

}
