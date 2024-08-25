package com.kamikazejam.kamicommon.nms.reflection;

import java.lang.reflect.Field;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class FieldHandle<T> {
    private final String fieldName;
    private final Class<?> clazz;
    private final Field field;

    public FieldHandle(final String fieldName, final Class<?> clazz) {
        this.fieldName = fieldName;
        this.clazz = clazz;
        try {
            (this.field = clazz.getDeclaredField(fieldName)).setAccessible(true);
        } catch (final NoSuchFieldException exc) {
            throw new RuntimeException(exc);
        }
    }

    public void set(final Object object, final Object value) {
        try {
            this.field.set(object, value);
        } catch (final IllegalAccessException ex) {
            throw new RuntimeException(String.format("Couldn't set value of field %s!", this.field.getName()));
        }
    }

    @SuppressWarnings("unchecked")
    public T get(final Object object) {
        try {
            return (T) this.field.get(object);
        } catch (final IllegalAccessException ex) {
            throw new RuntimeException(String.format("Couldn't retrieve value of field %s!", this.field.getName()));
        }
    }
}
