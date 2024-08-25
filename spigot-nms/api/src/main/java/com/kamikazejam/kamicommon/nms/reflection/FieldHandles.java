package com.kamikazejam.kamicommon.nms.reflection;

import com.google.common.collect.Maps;

import java.util.Map;

public class FieldHandles {
    private static final Map<String, FieldHandle<?>> handleCache;

    static {
        handleCache = Maps.newHashMap();
    }

    private static String getSerializedName(final String fieldName, final Class<?> clazz) {
        return String.format("%s+%s", fieldName, clazz.getName());
    }

    public static FieldHandle<?> getHandle(final String fieldName, final Class<?> clazz) {
        final String serialized = getSerializedName(fieldName, clazz);
        if (FieldHandles.handleCache.containsKey(serialized)) {
            return FieldHandles.handleCache.get(serialized);
        }
        final FieldHandle<?> newHandle = new FieldHandle<Object>(fieldName, clazz);
        FieldHandles.handleCache.put(serialized, newHandle);
        return newHandle;
    }
}
