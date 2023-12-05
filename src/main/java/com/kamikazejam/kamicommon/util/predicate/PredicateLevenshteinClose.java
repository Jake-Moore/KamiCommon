package com.kamikazejam.kamicommon.util.predicate;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class PredicateLevenshteinClose implements Predicate<String> {
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	private final String token;
	private final int levenshteinMax;

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	@Contract("_ -> new")
	public static @NotNull PredicateLevenshteinClose get(@NotNull String token) {
		return new PredicateLevenshteinClose(token);
	}

	@Contract(value = "_, _ -> new", pure = true)
	public static @NotNull PredicateLevenshteinClose get(@NotNull String token, int levenshteinMax) {
		return new PredicateLevenshteinClose(token, levenshteinMax);
	}

	@Contract("null -> fail")
	public PredicateLevenshteinClose(String token) {
		this(token, getLevenshteinMax(token));
	}

	@Contract(value = "null, _ -> fail", pure = true)
	public PredicateLevenshteinClose(String token, int levenshteinMax) {
		if (token == null) throw new NullPointerException("token");

		this.token = token;
		this.levenshteinMax = levenshteinMax;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public boolean apply(String type) {
		if (type == null) return false;
		int distance = StringUtils.getLevenshteinDistance(this.token, type);
		return distance <= this.levenshteinMax;
	}

	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //

	// This is the standard distance we tolerate for command aliases
	public static int getLevenshteinMax(String argument) {
		if (argument == null) return 0;
		if (argument.length() <= 1)
			return 0; // When dealing with 1 character aliases, there is way too many options. So we don't suggest.
		if (argument.length() <= 4)
			return 1; // When dealing with low length aliases, there too many options. So we won't suggest much
		if (argument.length() <= 7) return 2; // 2 is default.

		return 3; // If it were 8 characters or more, we end up here. Because many characters allow for more typos.
	}

}
