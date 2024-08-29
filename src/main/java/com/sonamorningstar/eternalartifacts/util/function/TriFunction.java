package com.sonamorningstar.eternalartifacts.util.function;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface TriFunction<A, B, C, R> {

    R apply(A a, B b, C c);

    @NotNull
    @Contract
    default <V> TriFunction<A, B, C, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (A a, B b, C c) -> after.apply(apply(a, b, c));
    }
}
