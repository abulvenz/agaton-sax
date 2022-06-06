package de.eismaenners.agatonsax;

import de.eismaenners.agatonsax.exceptions.UnexpectedElement;
import de.eismaenners.agatonsax.exceptions.UnexpectedEnd;
import de.eismaenners.agatonsax.utils.Stack;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DefaultHandlerImplementation extends DefaultHandler {

    private final Map<String, XMLElement<?, Void>> rootElementsByTag;
    private final Stack<ParsingContext> contextStack = new Stack<>();
    StringBuilder buildi;
    private final boolean VERBOSE = false;

    DefaultHandlerImplementation(Map<String, XMLElement<?, Void>> rootElementsByTag) {
        this.rootElementsByTag = rootElementsByTag;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        contextStack.push(new ParsingContext(rootElementsByTag));
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        XMLElement<?, ?> nextElement = contextStack.top().subElement(qName);

        if (nextElement == null) {
            throw new UnexpectedElement(qName, contextStack.top().expectedNames());
        }
        contextStack.push(new ParsingContextWithObject(nextElement));

        contextStack.top().createObject();
       
        if (VERBOSE) {
            System.out.println("Current stack: " + contextStack);
        }

        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.getQName(i);
            String value = attributes.getValue(i);
            contextStack.top().applyAttribute(name, value);
        }

        if (contextStack.top().expectedNames().isEmpty()) {
            buildi = new StringBuilder();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        if (buildi != null) {
            contextStack.top().addText(buildi.toString());
        }

        if (contextStack.size() > 2) {
            contextStack.top().reduce(contextStack.next());
        } else {
            contextStack.top().reduce(null);
        }

        contextStack.pop();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if (buildi != null) {
            buildi.append(ch, start, length);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        if (contextStack.size() != 1) {
            throw new UnexpectedEnd();
        }
    }

}
