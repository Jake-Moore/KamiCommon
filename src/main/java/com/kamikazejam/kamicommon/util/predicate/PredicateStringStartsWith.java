package com.kamikazejam.kamicommon.util.predicate;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class PredicateStringStartsWith implements Predicate<String> {
	private final @NotNull String prefix;

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	@Contract("_ -> new")
	public static @NotNull
	PredicateStringStartsWith get(@NotNull String prefix) {
		return new PredicateStringStartsWith(prefix);
	}

	public PredicateStringStartsWith(String prefix) {
		if (prefix == null) throw new NullPointerException("prefix");
		this.prefix = prefix.toLowerCase();
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public boolean apply(String str) {
		if (str == null) return false;
		return str.toLowerCase().startsWith(prefix);
	}

}
