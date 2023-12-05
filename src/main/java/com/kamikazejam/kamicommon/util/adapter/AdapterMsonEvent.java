package com.kamikazejam.kamicommon.util.adapter;

import com.google.gson.*;
import com.kamikazejam.kamicommon.util.mson.MsonEvent;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;

@SuppressWarnings("unused")
public class AdapterMsonEvent implements JsonDeserializer<MsonEvent>, JsonSerializer<MsonEvent> {
    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //

    private static final AdapterMsonEvent i = new AdapterMsonEvent();

    @Contract(pure = true)
    public static AdapterMsonEvent get() {
        return i;
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public JsonElement serialize(MsonEvent src, Type typeOfSrc, JsonSerializationContext context) {
        return MsonEvent.toJson(src);
    }

    @Override
    public MsonEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return MsonEvent.fromJson(json);
    }

}
