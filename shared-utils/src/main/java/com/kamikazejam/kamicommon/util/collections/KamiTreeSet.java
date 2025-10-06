package com.kamikazejam.kamicommon.util.collections;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * This subclass adds better constructors.
 * It also includes the comparator as a Generic for automatic use with GSON.
 */
@SuppressWarnings("unused")
public class KamiTreeSet<E, C extends Comparator<? super E>> extends TreeSet<E> {

    // -------------------------------------------- //
    // CONSTRUCT: BASE
    // -------------------------------------------- //

    @SuppressWarnings("unchecked")
    public KamiTreeSet(Object comparator) {
        super((comparator instanceof Comparator) ? (C) comparator : null);
    }

    public KamiTreeSet(Object comparator, Collection<? extends E> c) {
        // Support Null & this(comparator)
        this(comparator);
        if (c != null) addAll(c);
    }

    // -------------------------------------------- //
    // CONSTRUCT: EXTRA
    // -------------------------------------------- //

    @SafeVarargs
    public KamiTreeSet(Object comparator, E @NotNull ... elements) {
        this(comparator, Arrays.asList(elements));
    }

}
