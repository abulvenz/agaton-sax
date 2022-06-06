package de.eismaenners.elements;

public class MutableObject<T> {

    T object;

    public void setObject(T object) {
        this.object = object;
    }

    public T getObject() {
        return object;
    }

}
