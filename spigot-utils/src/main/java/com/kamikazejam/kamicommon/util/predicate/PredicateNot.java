package com.kamikazejam.kamicommon.util.predicate;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class PredicateNot<T> implements Predicate<T> {
	private final @NotNull Predicate<? super T> predicate;

	// -------------------------------------------- //
	// INSTANCE
	// -------------------------------------------- //

	@Contract(value = "_ -> new", pure = true)
	public static <T> @NotNull PredicateNot<T> get(@NotNull Predicate<? super T> predicate) {
		return new PredicateNot<>(predicate);
	}

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public PredicateNot(@NotNull Predicate<? super T> predicate) {
		this.predicate = predicate;
	}

	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	public @NotNull
	Predicate<? super T> getPredicate() {
		return this.predicate;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public boolean apply(T type) {
		return !this.getPredicate().apply(type);
	}

}
