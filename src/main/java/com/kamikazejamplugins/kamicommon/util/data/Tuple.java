package com.kamikazejamplugins.kamicommon.util.data;

import java.util.Objects;

/**
 * This is just a helpful class to store an object of three objects. Called a Tuple.
 */
@SuppressWarnings("unused")
public class Tuple<A, B, C> {
    private final A a;
    private final B b;
    private final C c;

    private Tuple(A a, B b, C c){
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public static <A, B, C> Tuple<A, B, C> of(A a, B b, C c) {
        return new Tuple<>(a, b, c);
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    public C getC() {
        return c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?, ?, ?> pair = (Tuple<?, ?, ?>) o;
        return Objects.equals(a, pair.a) && Objects.equals(b, pair.b) && Objects.equals(c, pair.c);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, c);
    }
}
