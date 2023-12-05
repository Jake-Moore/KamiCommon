package com.kamikazejam.kamicommon.util;

import com.kamikazejam.kamicommon.util.predicate.Predicate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ReflectionUtil {
    // -------------------------------------------- //
    // CONSTANTS
    // -------------------------------------------- //

    private static final Class<?>[] EMPTY_ARRAY_OF_CLASS = {};
    private static final Object[] EMPTY_ARRAY_OF_OBJECT = {};

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    @SuppressWarnings("unchecked")
    @Contract("null, _ -> fail")
    public static <T> @NotNull Constructor<T> getConstructor(Class<?> clazz, Class<?>... parameterTypes) {
        try {
            Constructor<T> ret = (Constructor<T>) clazz.getDeclaredConstructor(parameterTypes);
            makeAccessible(ret);
            return ret;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // -------------------------------------------- //
    // MAKE ACCESSIBLE
    // -------------------------------------------- //

    @Contract(value = "null -> fail")
    public static void makeAccessible(Field field) {
        try {
            // Mark as accessible using reflection.
            field.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Contract(value = "null -> fail")
    public static void makeAccessible(Constructor<?> constructor) {
        try {
            // Mark as accessible using reflection.
            constructor.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Contract(value = "null -> fail")
    public static void makeAccessible(Method method) {
        try {
            // Mark as accessible using reflection.
            method.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> getSuperclassDeclaringMethod(@NotNull Class<?> clazz, boolean includeSelf, final String methodName) {
        return getSuperclassPredicate(clazz, includeSelf, clazz1 -> {
            for (Method method : clazz1.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) return true;
            }
            return false;
        });
    }

    public static @Nullable Class<?> getSuperclassPredicate(@NotNull Class<?> clazz, boolean includeSelf, @NotNull Predicate<Class<?>> predicate) {
        for (Class<?> superClazz : getSuperclasses(clazz, includeSelf)) {
            if (predicate.apply(superClazz)) return superClazz;
        }
        return null;
    }

    // -------------------------------------------- //
    // SUPERCLASSES
    // -------------------------------------------- //
    public static @NotNull List<Class<?>> getSuperclasses(@NotNull Class<?> clazz, boolean includeSelf) {
        // Create
        List<Class<?>> ret = new ArrayList<>();

        // Fill
        if (!includeSelf) clazz = clazz.getSuperclass();
        while (clazz != null) {
            ret.add(clazz);
            clazz = clazz.getSuperclass();
        }

        // Return
        return ret;
    }

    // -------------------------------------------- //
    // ANNOTATION
    // -------------------------------------------- //

    @Contract("null, _ -> fail; !null, null -> fail")
    public static <T extends Annotation> @Nullable T getAnnotation(Field field, Class<T> annotationClass) {
        // Fail Fast
        if (field == null) throw new NullPointerException("field");
        if (annotationClass == null) throw new NullPointerException("annotationClass");

        try {
            return field.getAnnotation(annotationClass);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    // -------------------------------------------- //
    // SINGLETON INSTANCE
    // -------------------------------------------- //

    public static <T> @NotNull T getSingletonInstance(@NotNull Class<?> clazz) {
        Method get = getMethod(clazz, "get");
        T ret = invokeMethod(get, null);
        if (ret == null) throw new NullPointerException("Singleton instance was null for: " + clazz);
        if (!clazz.isAssignableFrom(ret.getClass()))
            throw new IllegalStateException("Singleton instance was not of same or subclass for: " + clazz);
        return ret;
    }


    // -------------------------------------------- //
    // METHOD
    // -------------------------------------------- //

    @Contract("null, _, _ -> fail; !null, null, _ -> fail")
    public static @NotNull Method getMethod(Class<?> clazz, String name, @NotNull Class<?>... parameterTypes) {
        try {
            Method ret = clazz.getDeclaredMethod(name, parameterTypes);
            makeAccessible(ret);
            return ret;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static @NotNull Method getMethod(@NotNull Class<?> clazz, @NotNull String name) {
        return getMethod(clazz, name, EMPTY_ARRAY_OF_CLASS);
    }

    @SuppressWarnings("unchecked")
    @Contract("null, _, _ -> fail")
    public static <T> T invokeMethod(Method method, @Nullable Object target, Object... arguments) {
        try {
            return (T) method.invoke(target, arguments);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T invokeMethod(@NotNull Method method, @Nullable Object target, @Nullable Object argument) {
        return invokeMethod(method, target, new Object[]{argument});
    }

    public static <T> T invokeMethod(@NotNull Method method, @Nullable Object target) {
        return invokeMethod(method, target, EMPTY_ARRAY_OF_OBJECT);
    }


    // -------------------------------------------- //
    // TYPE CHECKS
    // -------------------------------------------- //

    public static boolean isRawTypeAssignableFromAny(Type goal, Type @NotNull ... subjects) {
        // Cache this value since it will save us calculations
        Class<?> classGoal = classify(goal);

        for (Type t : subjects) {
            if (isRawTypeAssignableFrom(classGoal, t)) return true;
        }
        return false;
    }

    @Contract("null, _ -> false; !null, null -> false")
    public static boolean isRawTypeAssignableFrom(Type a, Type b) {
        if (a == null || b == null) return false;

        // Yes, this is a different sense of "Classifying"
        Class<?> classifiedA = classify(a);
        Class<?> classifiedB = classify(b);

        // In case one of the methods failed to retrieve a class
        if (classifiedA == null || classifiedB == null) return a.equals(b);

        return classifiedA.isAssignableFrom(classifiedB);
    }

    private static @Nullable Class<?> classify(Type type) {
        // Use loop structure rather than recursion to avoid stack size issues
        while (!(type instanceof Class)) {
            // Check for parameterized type
            if (!(type instanceof ParameterizedType)) return null;
            type = ((ParameterizedType) type).getRawType();
        }
        return (Class<?>) type;
    }

    // -------------------------------------------- //
    // FIELD > GET
    // -------------------------------------------- //

    @Contract("null, _ -> fail; !null, null -> fail")
    public static @NotNull Field getField(Class<?> clazz, String name) {
        if (clazz == null) throw new NullPointerException("clazz");
        if (name == null) throw new NullPointerException("name");
        try {
            Field ret = clazz.getDeclaredField(name);
            makeAccessible(ret);
            return ret;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getField(@NotNull Field field, Object object) {
        try {
            return (T) field.get(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
