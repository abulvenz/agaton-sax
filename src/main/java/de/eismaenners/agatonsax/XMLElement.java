package de.eismaenners.agatonsax;

import de.eismaenners.agatonsax.exceptions.UnambiguousElementTag;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class XMLElement<OwnType, ParentType> implements XMLContent<OwnType, ParentType> {

    private final String tag;
    private final Class<OwnType> clasz;
    private final Supplier<OwnType> creator;
    private final BiConsumer< ParentType, OwnType> whenParsed;
    private final Map<String, XMLElement<?, OwnType>> subElementsByTag = new HashMap<>();
    private final Map<String, XMLAttribute<?, OwnType>> attributesByName = new HashMap<>();
    private XMLData<?, OwnType> xmlData = null;

    public XMLElement(
            String tag,
            Class<OwnType> clasz,
            Supplier<OwnType> creator,
            BiConsumer< ParentType, OwnType> whenParsed
    ) {
        this.tag = tag;
        this.clasz = clasz;
        this.creator = creator;
        this.whenParsed = whenParsed;
    }

    <C> void addElement(XMLElement<C, OwnType> subElement) {
        if (subElementsByTag.containsKey(subElement.tag)) {
            throw new UnambiguousElementTag(subElement.tag, tag);
        }
        subElementsByTag.put(subElement.tag, subElement);
    }

    public <C> XMLElement<OwnType, ParentType>
            addAttribute(
                    String name,
                    Class<C> clasz,
                    Function<String, C> parse,
                    BiConsumer< OwnType, C> whenParsed
            ) {
        XMLAttribute<C, OwnType> attribute = new XMLAttribute<>(name, clasz, parse, whenParsed);
        attributesByName.put(name, attribute);
        return this;
    }

    Map<String, XMLElement<?, OwnType>> subElements() {
        return subElementsByTag;
    }

    public Map<String, XMLAttribute<?, OwnType>> getAttributesByName() {
        return attributesByName;
    }

    public Supplier<OwnType> getCreator() {
        return creator;
    }

    public Class<OwnType> getClasz() {
        return clasz;
    }

    public String getTag() {
        return tag;
    }

    public BiConsumer< ParentType, OwnType> getWhenParsed() {
        return whenParsed;
    }

    public List<String> attributeNames() {
        return attributesByName
                .keySet()
                .stream()
                .collect(Collectors.toList());
    }

    public XMLData<?, OwnType> getXmlData() {
        return xmlData;
    }

    @Override
    public XMLContentType contentType() {
        return XMLContentType.ELEMENT;
    }

    <T> void addAttribute(XMLAttribute<T, OwnType> attribute) {
        attributesByName.put(attribute.getName(), attribute);
    }

    @Override
    public String print(String indent) {
        final String attributeString = attributesByName
                .values()
                .stream()
                .map(a -> a.print(indent + "  "))
                .collect(Collectors.joining(" "));
        final String tagContent = String.join(" ", Arrays.asList(tag, attributeString).stream()
                .filter(Predicate.not(String::isBlank))
                .collect(Collectors.toList()));

        String str = indent + "<" + tagContent + ">\n";

        for (XMLElement sub : subElementsByTag.values()) {
            if (sub == this) {
                str += indent + "  <" + tag + " ----> selfloop>\n";
            } else {
                str += sub.print(indent + "  ");
            }
        }
        str += indent + "</" + tag + ">\n";

        return str;
    }

    @Override
    public String toString() {
        return "<" + tag + " [" + clasz + "] sub [" + subElementsByTag.keySet() + "]>";
    }

}
