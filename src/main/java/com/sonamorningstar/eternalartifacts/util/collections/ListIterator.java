package com.sonamorningstar.eternalartifacts.util.collections;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class ListIterator<E> implements Iterator<E> {
    private final List<E> elements;
    private int currentIndex = 0;

    public ListIterator(List<E> elements) {
        this.elements = elements;
    }

    public E putFirst(E element) {
        if (currentIndex == 0) {
            elements.add(0, element);
            return element;
        } else throw new IllegalStateException("Cannot add element to the beginning of the list if the iterator is not at the beginning.");
    }

    @Override
    public boolean hasNext() {
        return currentIndex < elements.size();
    }

    @Override
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return elements.get(currentIndex++);
    }
}
