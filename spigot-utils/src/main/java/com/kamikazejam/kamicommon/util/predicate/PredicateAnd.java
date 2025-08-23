package com.kamikazejam.kamicommon.util.predicate;

import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@SuppressWarnings("unused")
public class PredicateAnd<T> implements Predicate<T> {
	// -------------------------------------------- //
	// INSTANCE
	// -------------------------------------------- //

	@Contract("_ -> new")
	@SafeVarargs
	public static <T> @NotNull PredicateAnd<T> get(Predicate<? super T>... predicates) {
		return new PredicateAnd<>(predicates);
	}

	@Contract("_ -> new")
	public static <T> @NotNull PredicateAnd<T> get(Collection<Predicate<? super T>> predicates) {
		return new PredicateAnd<>(predicates);
	}

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	@SafeVarargs
	public PredicateAnd(Predicate<? super T>... predicates) {
		this.predicates = Collections.unmodifiableList(Arrays.asList(predicates));
	}

	public PredicateAnd(Collection<Predicate<? super T>> predicates) {
		this.predicates = Collections.unmodifiableList(new ArrayList<>(predicates));
	}

	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	private final List<Predicate<? super T>> predicates;

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public boolean apply(T type) {
		for (Predicate<? super T> predicate : this.getPredicates()) {
			if (!predicate.apply(type)) return false;
		}
		return true;
	}

}
