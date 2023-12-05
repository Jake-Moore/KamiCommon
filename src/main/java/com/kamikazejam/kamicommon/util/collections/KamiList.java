package com.kamikazejam.kamicommon.util.collections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.*;

/**
 * This subclass adds better constructors.
 */
public class KamiList<E> extends ArrayList<E> {

    // -------------------------------------------- //
    // CONSTRUCT: BASE
    // -------------------------------------------- //

    public KamiList(@Range(from = 0, to = Integer.MAX_VALUE) int initialCapacity) {
        super(initialCapacity);
    }

    public KamiList() {
        super();
    }

    @SuppressWarnings("unchecked")
    public KamiList(@Nullable Collection<? extends E> c) {
        // Support Null
        super(c == null ? Collections.EMPTY_LIST : c);
    }

    // -------------------------------------------- //
    // CONSTRUCT: EXTRA
    // -------------------------------------------- //

    @SafeVarargs
    public KamiList(E @NotNull ... elements) {
        this(Arrays.asList(elements));
    }

    // -------------------------------------------- //
    // OPTIMIZE: REMOVE ALL & RETAIN ALL
    // -------------------------------------------- //
    // This will greatly reduce the complexity in cases with big sizes.

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        if (c instanceof List) c = new HashSet<Object>(c);
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        if (c instanceof List) c = new HashSet<Object>(c);
        return super.retainAll(c);
    }
}

