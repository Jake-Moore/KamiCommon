package com.kamikazejam.kamicommon.util.collections;

import com.kamikazejam.kamicommon.util.KUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.Map.Entry;

/**
 * The ContainerUtil provides an imaginary super class to Collection and Map.
 * In Java they do not have a common interface yet many methods are similar and exists in both.
 * This some times results in twice the amount of source code, which we aim to remedy with this utility class.
 * <p>
 * We take an approach where we largely see a Map as a Collection of entries.
 * The "Container" class is simply an Object.
 * The return values are auto cast generics.
 * <p>
 * We have also added some information gatherers related to sorting and order.
 */
@SuppressWarnings("unused")
public class ContainerUtil {
    // -------------------------------------------- //
    // IS > CORE
    // -------------------------------------------- //

    @Contract(value = "null -> false", pure = true)
    public static boolean isContainer(Object container) {
        return isCollection(container) || isMap(container);
    }

    @Contract(value = "null -> false", pure = true)
    public static boolean isCollection(Object container) {
        return container instanceof Collection;
    }

    @Contract(value = "null -> false", pure = true)
    public static boolean isMap(Object container) {
        return container instanceof Map;
    }

    @Contract(value = "null -> false", pure = true)
    public static boolean isList(Object container) {
        return container instanceof List;
    }

    @Contract(value = "null -> false", pure = true)
    public static boolean isSet(Object container) {
        return container instanceof Set;
    }

    // -------------------------------------------- //
    // IS > BEHAVIOR
    // -------------------------------------------- //

    @Contract(value = "null -> false", pure = true)
    public static boolean isIndexed(Object container) {
        return isOrdered(container) || isSorted(container);
    }

    @Contract(value = "null -> false", pure = true)
    public static boolean isOrdered(Object container) {
        return container instanceof List || container instanceof LinkedHashMap || container instanceof LinkedHashSet;
    }

    @Contract(value = "null -> false", pure = true)
    public static boolean isSorted(Object container) {
        return container instanceof SortedSet || container instanceof SortedMap;
    }

    // -------------------------------------------- //
    // AS > CORE
    // -------------------------------------------- //

    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <C extends Collection<?>> @Nullable C asCollection(@Nullable Object container) {
        if (!isCollection(container)) return null;
        return (C) container;
    }

    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <M extends Map<?, ?>> @Nullable M asMap(@Nullable Object container) {
        if (!isMap(container)) return null;
        return (M) container;
    }

    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <S extends Set<?>> @Nullable S asSet(@Nullable Object container) {
        if (!isSet(container)) return null;
        return (S) container;
    }

    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <L extends List<?>> @Nullable L asList(@Nullable Object container) {
        if (!isList(container)) return null;
        return (L) container;
    }

    // -------------------------------------------- //
    // METHODS > SIZE
    // -------------------------------------------- //

    @Contract("null -> fail")
    public static boolean isEmpty(Object container) {
        if (container == null) throw new NullPointerException("container");

        Collection<Object> collection = asCollection(container);
        if (collection != null) {
            return collection.isEmpty();
        }

        Map<Object, Object> map = asMap(container);
        if (map != null) {
            return map.isEmpty();
        }

        throw new IllegalArgumentException(container.getClass().getName() + " is not a container.");
    }

    @Contract("null -> fail")
    public static @Range(from = 0, to = Integer.MAX_VALUE) int size(Object container) {
        if (container == null) throw new NullPointerException("container");

        Collection<Object> collection = asCollection(container);
        if (collection != null) {
            return collection.size();
        }

        Map<Object, Object> map = asMap(container);
        if (map != null) {
            return map.size();
        }

        throw new IllegalArgumentException(container.getClass().getName() + " is not a container.");
    }

    // -------------------------------------------- //
    // METHODS > GET
    // -------------------------------------------- //

    @Contract("null -> fail")
    @SuppressWarnings("unchecked")
    public static <E> @NotNull Collection<E> getElements(Object container) {
        if (container == null) throw new NullPointerException("container");

        Collection<E> collection = asCollection(container);
        if (collection != null) {
            return collection;
        }

        Map<Object, Object> map = asMap(container);
        if (map != null) {
            return (Collection<E>) map.entrySet();
        }

        throw new IllegalArgumentException(container.getClass().getName() + " is not a container.");
    }

    // -------------------------------------------- //
    // METHODS > SET
    // -------------------------------------------- //

    @Contract(value = "null -> fail", mutates = "param1")
    public static void clear(Object container) {
        if (container == null) throw new NullPointerException("container");

        Collection<Object> collection = asCollection(container);
        if (collection != null) {
            collection.clear();
            return;
        }

        Map<Object, Object> map = asMap(container);
        if (map != null) {
            map.clear();
            return;
        }

        throw new IllegalArgumentException(container.getClass().getName() + " is not a container.");
    }

    @Contract(mutates = "param1")
    public static void setElements(@NotNull Object container, @NotNull Iterable<?> elements) {
        clear(container);
        addElements(container, elements);
    }

    @Contract(value = "null, _ -> fail", mutates = "param1")
    @SuppressWarnings("unchecked")
    public static boolean addElement(Object container, Object element) {
        if (container == null) throw new NullPointerException("container");

        Collection<Object> collection = asCollection(container);
        if (collection != null) {
            return collection.add(element);
        }

        Map<Object, Object> map = asMap(container);
        if (map != null) {
            Entry<Object, Object> entry = (Entry<Object, Object>) element;
            Object key = entry.getKey();
            Object after = entry.getValue();
            Object before = map.put(key, after);
            return !KUtil.equals(after, before);
        }

        throw new IllegalArgumentException(container.getClass().getName() + " is not a container.");
    }

    @Contract(value = "null, _ -> fail; !null, null -> fail", mutates = "param1")
    public static void addElements(Object container, Iterable<?> elements) {
        if (container == null) throw new NullPointerException("container");
        if (elements == null) throw new NullPointerException("elements");

        for (Object element : elements) {
            addElement(container, element);
        }
    }

    // -------------------------------------------- //
    // ADDITIONS & DELETIONS
    // -------------------------------------------- //

    public static <E> @NotNull Collection<E> getAdditions(@NotNull Object before, @NotNull Object after) {
        Collection<E> elements = ContainerUtil.getElements(after);
        Set<E> ret = new KamiSet<>(elements);
        ret.removeAll(ContainerUtil.getElements(before));
        return ret;
    }

    public static <E> @NotNull Collection<E> getDeletions(@NotNull Object before, @NotNull Object after) {
        Collection<E> elements = ContainerUtil.getElements(before);
        Set<E> ret = new KamiSet<>(elements);
        ret.removeAll(ContainerUtil.getElements(after));
        return ret;
    }

    // -------------------------------------------- //
    // COPY
    // -------------------------------------------- //

    // For this method we must make a distinction between list and set.
    @SuppressWarnings("unchecked")
    public static <V> @Nullable V getCopy(@Nullable V container) {
        List<Object> list = asList(container);
        if (list != null) {
            return (V) new KamiList<>(list);
        }

        Set<Object> set = asSet(container);
        if (set != null) {
            return (V) new KamiSet<>(set);
        }

        Collection<Object> collection = asCollection(container);
        if (collection != null) {
            // Use list as fallback, when neither list nor set.
            return (V) new KamiList<>(collection);
        }

        return null;
    }

}
