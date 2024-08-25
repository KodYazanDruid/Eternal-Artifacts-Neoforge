package com.sonamorningstar.eternalartifacts.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface MenuConstructor<A, B, C, D, E, M> {

    M apply(A a, B b, C c, D d, E e);

    @NotNull
    @Contract
    default <V> MenuConstructor<A, B, C, D, E, V> andThen(Function<? super M, ? extends V> after) {
        Objects.requireNonNull(after);
        return (A a, B b, C c, D d, E e) -> after.apply(apply(a, b, c, d, e));
    }
}
