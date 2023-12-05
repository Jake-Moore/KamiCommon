package com.kamikazejam.kamicommon.util.collections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.*;

/**
 * This subclass adds better constructors.
 */
@SuppressWarnings("unused")
public class KamiMap<K, V> extends LinkedHashMap<K, V> {

	// -------------------------------------------- //
	// CONSTRUCT: BASE
	// -------------------------------------------- //

	public KamiMap(@Range(from = 0, to = Integer.MAX_VALUE) int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public KamiMap(@Range(from = 0, to = Integer.MAX_VALUE) int initialCapacity) {
		super(initialCapacity);
	}

	public KamiMap() {
		super();
	}

	@SuppressWarnings("unchecked")
	public KamiMap(Map<? extends K, ? extends V> m) {
		// Support Null
		super(m == null ? Collections.EMPTY_MAP : m);
	}

	public KamiMap(@Range(from = 0, to = Integer.MAX_VALUE) int initialCapacity, float loadFactor, boolean accessOrder) {
		super(initialCapacity, loadFactor, accessOrder);
	}

	// -------------------------------------------- //
	// CONSTRUCT: EXTRA
	// -------------------------------------------- //

	public KamiMap(K key1, V value1, Object @NotNull ... objects) {
		this(varargCreate(key1, value1, objects));
	}

	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //

	@SuppressWarnings("unchecked")
	public static <K, V> @NotNull KamiMap<K, V> varargCreate(K key1, V value1, Object @NotNull ... objects) {
		KamiMap<K, V> ret = new KamiMap<>();

		ret.put(key1, value1);

		Iterator<Object> iter = Arrays.asList(objects).iterator();
		while (iter.hasNext()) {
			K key = (K) iter.next();
			V value = (V) iter.next();
			ret.put(key, value);
		}

		return ret;
	}

	// -------------------------------------------- //
	// METHODS
	// -------------------------------------------- //

	public V set(K key, V value) {
		if (value == null) {
			return this.remove(key);
		} else {
			return this.put(key, value);
		}
	}

}
