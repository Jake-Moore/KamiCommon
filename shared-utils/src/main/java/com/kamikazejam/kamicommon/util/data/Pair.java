package com.kamikazejam.kamicommon.util.data;

import lombok.Data;
import lombok.Getter;

import java.util.Objects;

/**
 * This is just a helpful class to store an object of two objects. Called a Pair.
 */
@Getter
@SuppressWarnings("unused")
@Data
public class Pair<A, B> {
    private final A a;
    private final B b;

    private Pair(A a, B b){
        this.a = a;
        this.b = b;
    }

    public static <A, B> Pair<A, B> of(A a, B b) {
        return new Pair<>(a, b);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(a, pair.a) && Objects.equals(b, pair.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }
}
