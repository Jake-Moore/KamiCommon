package com.kamikazejam.kamicommon.command.type;

import com.kamikazejam.kamicommon.command.editor.annotation.EditorType;
import com.kamikazejam.kamicommon.command.editor.annotation.EditorTypeInner;
import com.kamikazejam.kamicommon.command.type.combined.TypeEntry;
import com.kamikazejam.kamicommon.command.type.container.TypeList;
import com.kamikazejam.kamicommon.command.type.container.TypeMap;
import com.kamikazejam.kamicommon.command.type.container.TypeSet;
import com.kamikazejam.kamicommon.command.type.primitive.*;
import com.kamikazejam.kamicommon.command.type.sender.TypeOfflinePlayer;
import com.kamikazejam.kamicommon.command.type.sender.TypePlayer;
import com.kamikazejam.kamicommon.util.ReflectionUtil;
import com.kamikazejam.kamicommon.util.collections.KamiList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class RegistryType {
    // -------------------------------------------- //
    // REGISTRY
    // -------------------------------------------- //

    private static final Map<Class<?>, Type<?>> registry = new HashMap<>();

    @Contract("null, _ -> fail; !null, null -> fail")
    public static <T> void register(Class<T> clazz, Type<? super T> type) {
        if (clazz == null) throw new NullPointerException("clazz");
        if (type == null) throw new NullPointerException("type");
        registry.put(clazz, type);
    }

    @Contract("null -> fail")
    public static <T> void register(Type<T> type) {
        if (type == null) throw new NullPointerException("type");
        register(type.getClazz(), type);
    }

    @Contract("null -> fail")
    @SuppressWarnings("unchecked")
    public static <T> Type<? super T> unregister(Class<T> clazz) {
        if (clazz == null) throw new NullPointerException("clazz");
        return (Type<T>) registry.remove(clazz);
    }

    @Contract("null -> fail")
    public static boolean isRegistered(Class<?> clazz) {
        if (clazz == null) throw new NullPointerException("clazz");
        return registry.containsKey(clazz);
    }

    // -------------------------------------------- //
    // GET TYPE
    // -------------------------------------------- //

    @Contract("null, null, _ -> fail")
    public static @Nullable Type<?> getType(Field field, java.lang.reflect.Type fieldType, boolean strictThrow) {
        if (field != null) {
            try {
                EditorType annotationType = ReflectionUtil.getAnnotation(field, EditorType.class);
                if (annotationType != null) {
                    Class<?> typeClass = annotationType.value();
                    return ReflectionUtil.getSingletonInstance(typeClass);
                }
            } catch (Throwable t) {
                // This has to do with backwards compatibility (Usually 1.7).
                // The annotations may trigger creation of type class instances.
                // Those type classes may refer to Bukkit classes not present.
                // This issue was first encountered for TypeDataItemStack.
            }

            if (fieldType == null) {
                fieldType = field.getGenericType();
            }
        }

        if (fieldType != null) {
            if (fieldType instanceof ParameterizedType) {
                Class<?> fieldClass = field == null ? null : field.getType();
                List<Type<?>> innerTypes;

                if (ReflectionUtil.isRawTypeAssignableFromAny(List.class, fieldType, fieldClass)) {
                    innerTypes = getInnerTypes(field, fieldType, 1);
                    return TypeList.get(innerTypes.get(0));
                }

                if (ReflectionUtil.isRawTypeAssignableFromAny(Set.class, fieldType, fieldClass)) {
                    innerTypes = getInnerTypes(field, fieldType, 1);
                    return TypeSet.get(innerTypes.get(0));
                }

                if (ReflectionUtil.isRawTypeAssignableFromAny(Map.Entry.class, fieldType, fieldClass)) {
                    innerTypes = getInnerTypes(field, fieldType, 2);
                    return TypeEntry.get(innerTypes.get(0), innerTypes.get(1));
                }

                if (ReflectionUtil.isRawTypeAssignableFromAny(Map.class, fieldType, fieldClass)) {
                    innerTypes = getInnerTypes(field, fieldType, 2);
                    return TypeMap.get(innerTypes.get(0), innerTypes.get(1));
                }

                if (strictThrow) throw new IllegalArgumentException("Unhandled ParameterizedType: " + fieldType);
                return null;
            }

            if (fieldType instanceof Class) {
                Type<?> type = registry.get(fieldType);
                if (strictThrow && type == null) throw new IllegalStateException(fieldType + " is not registered.");
                return type;
            }

            throw new IllegalArgumentException("Neither ParameterizedType nor Class: " + fieldType);
        }

        throw new IllegalArgumentException("No Information Supplied");
    }

    public static Type<?> getType(@NotNull Field field, boolean strictThrow) {
        return getType(field, null, strictThrow);
    }

    public static Type<?> getType(@NotNull java.lang.reflect.Type fieldType, boolean strictThrow) {
        return getType(null, fieldType, strictThrow);
    }

    public static Type<?> getType(@NotNull Field field) {
        return getType(field, true);
    }

    public static Type<?> getType(@NotNull java.lang.reflect.Type fieldType) {
        return getType(fieldType, true);
    }

    // -------------------------------------------- //
    // GET INNER TYPES
    // -------------------------------------------- //

    public static @NotNull List<Type<?>> getInnerTypes(Field field, java.lang.reflect.Type fieldType, int amountRequired) {
        // Annotation
        if (field != null) {
            try {
                EditorTypeInner annotation = ReflectionUtil.getAnnotation(field, EditorTypeInner.class);
                if (annotation != null) {
                    // Create
                    List<Type<?>> ret = new KamiList<>();

                    // Fill
                    Class<?>[] innerTypeClasses = annotation.value();
                    for (Class<?> innerTypeClass : innerTypeClasses) {
                        Type<?> innerType = ReflectionUtil.getSingletonInstance(innerTypeClass);
                        ret.add(innerType);
                    }

                    // Return
                    return ret;
                }
            } catch (Throwable t) {
                // This has to do with backwards compatibility (Usually 1.7).
                // The annotations may trigger creation of type class instances.
                // Those type classes may refer to Bukkit classes not present.
                // This issue was first encountered for TypeDataItemStack.
            }

            if (fieldType == null) {
                fieldType = field.getGenericType();
            }
        }

        // Reflection
        if (fieldType != null) {
            if (fieldType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) fieldType;
                // Create
                List<Type<?>> ret = new KamiList<>();

                // Fill
                int count = 0;
                for (java.lang.reflect.Type actualTypeArgument : parameterizedType.getActualTypeArguments()) {
                    boolean strictThrow = (amountRequired < 0 || count < amountRequired);
                    Type<?> innerType = getType(actualTypeArgument, strictThrow);
                    ret.add(innerType);
                    count++;
                }

                // Return
                return ret;
            }

            throw new IllegalArgumentException("Not ParameterizedType: " + fieldType);
        }

        throw new IllegalArgumentException("Failure");
    }

    // -------------------------------------------- //
    // DEFAULTS
    // -------------------------------------------- //

    public static void registerAll() {
        // Primitive
        register(Boolean.TYPE, TypeBooleanTrue.get());
        register(Boolean.class, TypeBooleanTrue.get());

        register(Byte.TYPE, TypeByte.get());
        register(Byte.class, TypeByte.get());

        register(Double.TYPE, TypeDouble.get());
        register(Double.class, TypeDouble.get());

        register(Float.TYPE, TypeFloat.get());
        register(Float.class, TypeFloat.get());

        register(Integer.TYPE, TypeInteger.get());
        register(Integer.class, TypeInteger.get());

        register(Long.TYPE, TypeLong.get());
        register(Long.class, TypeLong.get());

        register(TypeString.get());


        // Bukkit
        register(TypeItemStack.get());
        register(TypeUUID.get());

        register(TypePotionEffectType.get());
        register(TypeNamespacedKey.get());
        register(TypeEnchantment.get());

        register(TypePermission.get());
        register(TypeWorld.get());

        // Sender
        register(TypePlayer.get());
        register(TypeOfflinePlayer.get());
    }

}
