package com.kamikazejam.kamicommon.util.adapter;

import com.google.gson.*;
import com.kamikazejam.kamicommon.util.mson.Mson;
import com.kamikazejam.kamicommon.util.mson.MsonEvent;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;

@SuppressWarnings("unused")
public class AdapterMsonEventFix implements JsonDeserializer<MsonEvent>, JsonSerializer<MsonEvent> {
    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //

    private static final AdapterMsonEventFix i = new AdapterMsonEventFix();

    @Contract(pure = true)
    public static AdapterMsonEventFix get() {
        return i;
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public JsonElement serialize(MsonEvent src, Type typeOfSrc, JsonSerializationContext context) {
        return Mson.getGson(false).toJsonTree(src);
    }

    @Override
    public MsonEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        MsonEvent ret = Mson.getGson(false).fromJson(json, MsonEvent.class);
        ret.repair();
        return ret;
    }

}
