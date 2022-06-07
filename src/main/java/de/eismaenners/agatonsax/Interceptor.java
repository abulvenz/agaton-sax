package de.eismaenners.agatonsax;

import java.util.function.Consumer;

public class Interceptor<T> {

    String path;
    Class<T> clasz;
    Consumer<T> whenParsed;

    public Interceptor(String path, Class<T> clasz, Consumer<T> whenParsed) {
        this.path = path;
        this.clasz = clasz;
        this.whenParsed = whenParsed;
    }

    void intercept(Object object, Object parent) {
        whenParsed.accept(cast(object));
    }

    T cast(Object obj) {
        return clasz.cast(obj);
    }
}
