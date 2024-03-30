package com.kamikazejam.kamicommon.util.comparator;

import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Getter
@SuppressWarnings("unused")
public class ComparatorCombined<T> extends ComparatorAbstract<T> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	@Contract("_ -> new")
	@SafeVarargs
	public static <T> @NotNull ComparatorCombined<T> get(Comparator<? super T>... comparators) {
		return new ComparatorCombined<>(comparators);
	}

	@Contract("_ -> new")
	public static <T> @NotNull ComparatorCombined<T> get(List<Comparator<? super T>> comparators) {
		return new ComparatorCombined<>(comparators);
	}

	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	private List<Comparator<? super T>> comparators;

	public ComparatorCombined<T> setComparators(List<Comparator<? super T>> comparators) {
		this.comparators = comparators;
		return this;
	}

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	@SafeVarargs
	public ComparatorCombined(Comparator<? super T> @NotNull ... comparators) {
		this(Arrays.asList(comparators));
	}

	public ComparatorCombined(List<Comparator<? super T>> comparators) {
		this.comparators = comparators;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public int compareInner(T object1, T object2) {
		for (Comparator<? super T> comparator : this.getComparators()) {
			int ret = comparator.compare(object1, object2);
			if (ret != 0) return ret;
		}
		return 0;
	}

}
