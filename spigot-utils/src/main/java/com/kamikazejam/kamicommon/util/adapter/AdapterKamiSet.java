package com.kamikazejam.kamicommon.util.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.kamikazejam.kamicommon.util.collections.KamiSet;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;
import java.util.Collection;

public class AdapterKamiSet extends AdapterKamiX<KamiSet<?>> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final AdapterKamiSet i = new AdapterKamiSet();

	@Contract(pure = true)
	public static AdapterKamiSet get() {
		return i;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public KamiSet<?> create(Object parent, JsonElement json, Type typeOfT, JsonDeserializationContext context) {
		return new KamiSet((Collection) parent);
	}

}
