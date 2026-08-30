package com.kamikazejam.kamicommon.util.collections;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * This subclass adds better constructors.
 */
@SuppressWarnings("unused")
public class KamiSet<E> extends LinkedHashSet<E> {

    // -------------------------------------------- //
    // CONSTRUCT: BASE
    // -------------------------------------------- //

    public KamiSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public KamiSet(int initialCapacity) {
        super(initialCapacity);
    }

    public KamiSet() {
        super();
    }

    @SuppressWarnings("unchecked")
    public KamiSet(Collection<? extends E> c) {
        // Support Null
        super(c == null ? Collections.EMPTY_LIST : c);
    }

    // -------------------------------------------- //
    // CONSTRUCT: EXTRA
    // -------------------------------------------- //

    @SafeVarargs
    public KamiSet(E @NotNull ... elements) {
        this(Arrays.asList(elements));
    }

    // -------------------------------------------- //
    // OPTIMIZE: REMOVE ALL & RETAIN ALL
    // -------------------------------------------- //
    // This will greatly reduce the complexity in cases with big sizes.

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c instanceof List) c = new HashSet<Object>(c);
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        if (c instanceof List) c = new HashSet<Object>(c);
        return super.retainAll(c);
    }

}
