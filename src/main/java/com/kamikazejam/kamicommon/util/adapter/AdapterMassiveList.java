package com.kamikazejam.kamicommon.util.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.kamikazejam.kamicommon.util.collections.KamiList;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;
import java.util.Collection;

public class AdapterMassiveList extends AdapterMassiveX<KamiList<?>> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final AdapterMassiveList i = new AdapterMassiveList();

	@Contract(pure = true)
	public static AdapterMassiveList get() {
		return i;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public KamiList<?> create(Object parent, JsonElement json, Type typeOfT, JsonDeserializationContext context) {
		return new KamiList<>((Collection) parent);
	}
}
