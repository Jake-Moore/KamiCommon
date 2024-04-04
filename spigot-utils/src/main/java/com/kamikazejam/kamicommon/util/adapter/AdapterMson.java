package com.kamikazejam.kamicommon.util.adapter;

import com.google.gson.*;
import com.kamikazejam.kamicommon.util.mson.Mson;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;

@SuppressWarnings("unused")
public class AdapterMson implements JsonDeserializer<Mson>, JsonSerializer<Mson> {
    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //

    private static final AdapterMson i = new AdapterMson();

    @Contract(pure = true)
    public static AdapterMson get() {
        return i;
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public JsonElement serialize(Mson src, Type typeOfSrc, JsonSerializationContext context) {
        return Mson.toJson(src);
    }

    @Override
    public Mson deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Mson.fromJson(json);
    }

}
