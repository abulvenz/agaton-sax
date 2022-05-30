package de.eismaenners.agatonsax;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.xml.sax.helpers.DefaultHandler;

public final class AgatonSax {

    public static AgatonSax create() {
        return new AgatonSax();
    }

    public static <T, P> XMLElement<T, P> element(
            String tag,
            Class<T> clasz,
            Supplier<T> creator,
            BiConsumer<P, T> whenParsed,
            XMLContent<?, T>... subElements
    ) {
        final XMLElement element = new XMLElement(tag, clasz, creator, whenParsed);

        for (XMLContent<?, T> subElement : subElements) {
            if (subElement.contentType() == XMLContent.XMLContentType.ELEMENT) {
                element.addElement((XMLElement<?, T>) subElement);
            } else if (subElement.contentType() == XMLContent.XMLContentType.ATTRIBUTE) {
                element.addAttribute((XMLAttribute<?, T>) subElement);
            }
        }
        return element;
    }

    public static <T, P> XMLAttribute<T, P> attribute(
            String name,
            Class<T> clasz,
            Function<String, T> parser,
            BiConsumer< P, T> whenParsed
    ) {
        return new XMLAttribute<>(name, clasz, parser, whenParsed);
    }

    Map<String, XMLElement<?, Void>> rootElementsByTag = new HashMap<>();
    private AnnotationCreator annotationDecorator = new AnnotationCreator();

    public <T> AgatonSax addRootElement(String tag, Class<T> clasz, Supplier<T> creator,
            Consumer<T> whenParsed, XMLContent<?, T>... subElements) {

        XMLElement<T, Void> element = new XMLElement<>(tag, clasz, creator, (parent, obj) -> whenParsed.accept(obj));
        rootElementsByTag.put(tag, element);

        for (XMLContent<?, T> subElement : subElements) {
            if (subElement.contentType() == XMLContent.XMLContentType.ELEMENT) {
                element.addElement((XMLElement<?, T>) subElement);
            } else if (subElement.contentType() == XMLContent.XMLContentType.ATTRIBUTE) {
                element.addAttribute((XMLAttribute<?, T>) subElement);
            }
        }

        return this;
    }

    public DefaultHandler getHandler() {
        return new DefaultHandlerImplementation(rootElementsByTag);
    }

    public static <T> Consumer<T> NO_OP() {
        return t -> {
        };
    }

    public <T> AgatonSax addAnnotatedRootClass(Class<T> clasz, Consumer<T> whenParsed) {
        XMLElement<T, Void> element = annotationDecorator.createRoot(clasz, whenParsed);
        this.rootElementsByTag.put(element.getTag(), element);
        return this;
    }
}
