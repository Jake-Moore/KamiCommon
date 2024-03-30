package com.kamikazejam.kamicommon.util.predicate;

public interface Predicate<T> extends java.util.function.Predicate<T> {
	boolean apply(T type);

	@Override
	default boolean test(T type) {
		return apply(type);
	}
}
