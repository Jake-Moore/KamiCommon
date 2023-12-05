package com.kamikazejam.kamicommon.util.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.kamikazejam.kamicommon.util.collections.KamiTreeSet;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Collection;

public class AdapterMassiveTreeSet extends AdapterMassiveX<KamiTreeSet<?, ?>> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final AdapterMassiveTreeSet i = new AdapterMassiveTreeSet();

	@Contract(pure = true)
	public static AdapterMassiveTreeSet get() {
		return i;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public KamiTreeSet<?, ?> create(Object parent, JsonElement json, @NotNull Type typeOfT, JsonDeserializationContext context) {
		Object comparator = getComparator(typeOfT);
		return new KamiTreeSet(comparator, (Collection) parent);
	}

	// -------------------------------------------- //
	// GET COMPARATOR
	// -------------------------------------------- //

	public static @NotNull Object getComparator(@NotNull Type typeOfT) {
		return getNewArgumentInstance(typeOfT, 1);
	}

}
