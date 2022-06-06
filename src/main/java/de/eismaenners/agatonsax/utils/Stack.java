package de.eismaenners.agatonsax.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Stack<T> {

    List<T> elements = new LinkedList<>();

    public void push(T t) {
        elements.add(t);
    }

    public T top() {
        if (isEmpty()) {
            return null;
        } else {
            return elements.get(elements.size() - 1);
        }
    }

    public T next() {
        if (elements.size() < 2) {
            return null;
        } else {
            return elements.get(elements.size() - 2);
        }
    }

    public void pop() {
        if (!isEmpty()) {
            elements.remove(elements.size() - 1);
        }
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public int size() {
        return elements.size();
    }

    @Override
    public String toString() {
        return "\n"
                + stream()
                        .map(e -> "  " + e)
                        .collect(Collectors.joining("\n"))
                + "\n";
    }

    public Stream<T> stream() {
        return elements.stream();
    }

}
