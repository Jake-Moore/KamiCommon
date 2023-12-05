package com.kamikazejam.kamicommon.util.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.kamikazejam.kamicommon.util.collections.KamiMap;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;
import java.util.Map;

public class AdapterMassiveMap extends AdapterMassiveX<KamiMap<?, ?>> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final AdapterMassiveMap i = new AdapterMassiveMap();

	@Contract(pure = true)
	public static AdapterMassiveMap get() {
		return i;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public KamiMap<?, ?> create(Object parent, JsonElement json, Type typeOfT, JsonDeserializationContext context) {
		return new KamiMap((Map) parent);
	}

}
