package com.kamikazejam.kamicommon.util.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.kamikazejam.kamicommon.util.collections.KamiSet;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;
import java.util.Collection;

public class AdapterMassiveSet extends AdapterMassiveX<KamiSet<?>> {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final AdapterMassiveSet i = new AdapterMassiveSet();

	@Contract(pure = true)
	public static AdapterMassiveSet get() {
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
