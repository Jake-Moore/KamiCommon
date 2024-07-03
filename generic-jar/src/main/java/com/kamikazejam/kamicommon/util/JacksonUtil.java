package com.kamikazejam.kamicommon.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class JacksonUtil {

    private static @Nullable ObjectMapper mapper = null;
    public static @NotNull ObjectMapper getMapper() {
        if (mapper != null) return mapper;
        mapper = new ObjectMapper();
        // mapper.enable(SerializationFeature.INDENT_OUTPUT);  // Optional: enable pretty printing

        // Configure Jackson to only use fields for serialization (ignoring transient fields)
        //   We have to disable setters and getters, otherwise a transient getter or setter will cause it to be serialized
        VisibilityChecker.Std check = new VisibilityChecker.Std(
                JsonAutoDetect.Visibility.NONE,           // don't use getters for field mapping
                JsonAutoDetect.Visibility.NONE,           // don't use getters for field mapping
                JsonAutoDetect.Visibility.NONE,           // don't use setters for field mapping
                JsonAutoDetect.Visibility.NONE,           // don't use creators
                JsonAutoDetect.Visibility.ANY             // any field
        );
        mapper.setVisibility(check);

        // Enable serialization of null and empty values
        mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        return mapper;
    }

    @SneakyThrows
    public static @NotNull String serialize(@NotNull Object o) {
        return getMapper().writeValueAsString(o);
    }

    @SneakyThrows @Contract("_, !null -> !null")
    public static <X> @Nullable X deserialize(Class<X> clazz, @Nullable String json) {
        if (json == null) { return null; }
        return getMapper().readValue(json, clazz);
    }
}
