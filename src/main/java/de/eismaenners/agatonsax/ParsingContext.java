package de.eismaenners.agatonsax;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParsingContext<T, P> {

    protected final Map<String, XMLElement<?, T>> expectedElementsByTag;

    ParsingContext(Map<String, XMLElement<?, T>> expectedElementsByTag) {
        this.expectedElementsByTag = expectedElementsByTag;
    }

    XMLElement<?, ?> subElement(String qName) {
        return expectedElementsByTag
                .get(qName);
    }

    List<String> expectedNames() {
        return expectedElementsByTag
                .keySet()
                .stream()
                .collect(Collectors.toList());
    }

    void createObject() {
        throw new UnsupportedOperationException();
    }

    void applyAttribute(String name, String value) {
        throw new UnsupportedOperationException();
    }

    public void addText(String str) {
        throw new UnsupportedOperationException();
    }

    T getObject() {
        throw new UnsupportedOperationException();
    }

    void reduce(ParsingContext<P, ?> next) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return expectedElementsByTag.toString();
    }

    public String getPathFragment() {
        return "";
    }
}
