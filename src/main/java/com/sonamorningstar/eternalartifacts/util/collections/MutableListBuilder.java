package com.sonamorningstar.eternalartifacts.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MutableListBuilder<T> {
    private final List<T> list;

    public MutableListBuilder() {
        this.list = new ArrayList<>();
    }

    public MutableListBuilder<T> add(T element) {
        list.add(element);
        return this;
    }

    public MutableListBuilder<T> addAll(Collection<? extends T> elements) {
        list.addAll(elements);
        return this;
    }

    public List<T> build() {
        return list;
    }
}

