package com.kamikazejam.kamicommon.util.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.kamikazejam.kamicommon.util.collections.KamiMap;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;
import java.util.Map;

public class AdapterKamiMap extends AdapterKamiX<KamiMap<?, ?>> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final AdapterKamiMap i = new AdapterKamiMap();

	@Contract(pure = true)
	public static AdapterKamiMap get() {
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
