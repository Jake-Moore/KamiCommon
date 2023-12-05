package com.kamikazejam.kamicommon.util.adapter;

import com.google.gson.*;
import com.google.gson.internal.$Gson$Types;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * This is the abstract adapter for all "Massive structures".
 * It makes sure Def instances "handle empty as null".
 * It makes sure we avoid infinite GSON recurse loops by recursing with supertype.
 */
@SuppressWarnings("unused")
public abstract class AdapterMassiveX<T> implements JsonDeserializer<T>, JsonSerializer<T> {
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public JsonElement serialize(T src, Type type, JsonSerializationContext context) {
		ParameterizedType pType = (ParameterizedType) type;

		// This a regular Massive structure and not a Def ...
		//  Serialize it as if it were the regular java collection!
		//  SUPER TYPE x1 EXAMPLE: MassiveList --> ArrayList
		return context.serialize(src, getSuperType(pType));
	}

	@Override
	public T deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		ParameterizedType pType = (ParameterizedType) type;

		// ... and the json is null or a JsonNull ...
		if (json == null || json instanceof JsonNull) {
			// ... then deserialize as a null!
			return null;
		}
		// ... and it's non null and contains something ...
		else {
			// ... then deserialize it as if it were the regular java collection!
			// SUPER TYPE x1 EXAMPLE: MassiveList --> ArrayList
			Object parent = context.deserialize(json, getSuperType(pType));
			return create(parent, json, type, context);
		}
	}

	// -------------------------------------------- //
	// ABSTRACT
	// -------------------------------------------- //

	public abstract T create(Object parent, JsonElement json, Type typeOfT, JsonDeserializationContext context);

	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //

	public static Class<?> getClazz(@NotNull ParameterizedType pType) {
		return (Class<?>) pType.getRawType();
	}

	public static @NotNull ParameterizedType getSuperType(@NotNull ParameterizedType pType) {
		// ------- SELF -------

		// Get args
		Type[] args = pType.getActualTypeArguments();

		// Get clazz
		Class<?> clazz = (Class<?>) pType.getRawType();

		// ------- SUPER -------

		// Get sType
		ParameterizedType spType = (ParameterizedType) clazz.getGenericSuperclass();

		// Get sArgs
		// NOTE: These will be broken! we can however look at the count!
		Type[] sArgs = spType.getActualTypeArguments();

		// Get sClazz
		Class<?> sClazz = (Class<?>) spType.getRawType();

		// ------- CONSTRUCTED -------

		Type[] typeArguments = Arrays.copyOfRange(args, 0, sArgs.length);

		return $Gson$Types.newParameterizedTypeWithOwner(null, sClazz, typeArguments);
	}

	public static @NotNull Object getNewArgumentInstance(@NotNull Type type, int index) {
		ParameterizedType parameterizedType = (ParameterizedType) type;
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
		Class<?> clazz = (Class<?>) actualTypeArguments[index];
		try {
			return clazz.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Contract("null -> true")
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Object object) {
		// A Map is not a Collection.
		// Thus we have to use isEmpty() declared in different interfaces. 
		if (object == null) return true;
		if (object instanceof Map) return ((Map) object).isEmpty();
		if (object instanceof Collection) return ((Collection) object).isEmpty();
		return false;
	}

}
