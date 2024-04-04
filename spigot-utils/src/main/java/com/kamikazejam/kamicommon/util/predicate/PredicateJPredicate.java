package com.kamikazejam.kamicommon.util.predicate;

import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Getter
@SuppressWarnings("unused")
public class PredicateJPredicate<T> implements Predicate<T> {
	private final java.util.function.Predicate<? super T> predicate;

	public PredicateJPredicate(java.util.function.Predicate<? super T> predicate) {
		this.predicate = predicate;
	}

	// -------------------------------------------- //
	// INSTANCE
	// -------------------------------------------- //

	@Contract(value = "_ -> new", pure = true)
	public static <T> @NotNull PredicateJPredicate<T> get(java.util.function.Predicate<? super T> predicate) {
		return new PredicateJPredicate<>(predicate);
	}

	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public boolean apply(T type) {
		return this.getPredicate().test(type);
	}

}
