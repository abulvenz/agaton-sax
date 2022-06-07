package de.eismaenners.agatonsax;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
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
    private Map<String, Interceptor<?>> interceptorsByPath = new HashMap<>();

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

    public <T> AgatonSax addAnnotatedRootClass(Class<T> clasz, Consumer<T> whenParsed) {
        XMLElement<T, Void> element = annotationDecorator.createAnnotatedRoot(clasz, whenParsed);
        this.rootElementsByTag.put(element.getTag(), element);
        return this;
    }

    public <T> AgatonSax addRootClass(Class<T> clasz, Consumer<T> whenParsed) {
        XMLElement<T, Void> element = annotationDecorator.createRoot(clasz, whenParsed);
        this.rootElementsByTag.put(element.getTag(), element);
        return this;
    }

    public void parseString(String xml) {
        try {
            SAXParser parser = SAXParserFactory.newDefaultInstance().newSAXParser();
            parser.parse(new ByteArrayInputStream(xml.getBytes()), getHandler());
        } catch (SAXException ex) {
            Logger.getLogger(AgatonSax.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AgatonSax.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(AgatonSax.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public <T> AgatonSax addInterceptor(String path, Class<T> clasz, Consumer<T> withElement) {
        this.interceptorsByPath.put(path, new Interceptor(path, clasz, withElement));
        return this;
    }

    public DefaultHandler getHandler() {
        if (DefaultHandlerImplementation.VERBOSE) {
            rootElementsByTag.forEach((tag, element) -> System.out.println(tag + "\n----------\n" + element.print("")));
        }
        return new DefaultHandlerImplementation(rootElementsByTag,interceptorsByPath);
    }
}
