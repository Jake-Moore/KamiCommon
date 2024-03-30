package com.kamikazejam.kamicommon.util.comparator;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Map.Entry;

public class ComparatorEntryKey<K, V> extends ComparatorAbstractTransformer<Entry<K, V>, K> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	@Contract("_ -> new")
	public static <K, V> @NotNull ComparatorEntryKey<K, V> get(Comparator<K> comparator) {
		return new ComparatorEntryKey<>(comparator);
	}

	public ComparatorEntryKey(Comparator<K> comparator) {
		super(comparator);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public K transform(@NotNull Entry<K, V> type) {
		return type.getKey();
	}

}
