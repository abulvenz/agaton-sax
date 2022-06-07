package de.eismaenners.agatonsax;

import static de.eismaenners.agatonsax.AgatonSax.*;
import de.eismaenners.agatonsax.exceptions.AttributeMapperNotFound;
import de.eismaenners.agatonsax.exceptions.CannotCreateInstance;
import de.eismaenners.agatonsax.exceptions.FieldNotAccessible;
import de.eismaenners.agatonsax.exceptions.MissingAnnotation;
import de.eismaenners.agatonsax.utils.Reflection;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class AnnotationCreator {

    private static final String DEFAULT_NAME = "##default";

    Set<Class<?>> terminalClasses = new HashSet<>();
    MapperCreator mapperCreator = new MapperCreator();

    public AnnotationCreator() {
        mapperCreator.addDefaultMappers();
        addDefaultTerminalClasses();
    }

    protected void addDefaultTerminalClasses() {
        terminalClasses.addAll(Set.of(
                String.class,
                Integer.class,
                Float.class,
                Double.class,
                Boolean.class
        ));
    }

    public <C> void addMapper(Class<C> clasz, Function< String, C> mapper) {
        mapperCreator.addMapper(clasz, mapper);
    }

    <T> XMLElement<T, Void> createAnnotatedRoot(Class<T> clasz, Consumer<T> whenParsed) {
        XmlRootElement annotation = clasz.getAnnotation(XmlRootElement.class);
        if (annotation == null) {
            throw new MissingAnnotation("Root must be annotated");
        }
        return createRoot(clasz, whenParsed);
    }

    <T> XMLElement<T, Void> createRoot(Class<T> clasz, Consumer<T> whenParsed) {
        String name = determineRootName(clasz);

        XMLElement<T, Void> newElement = element(
                name,
                clasz,
                () -> safelyCreateDecoratee(clasz),
                (t, obj) -> whenParsed.accept(obj)
        );

        addFieldElementsAndAttributes(clasz, newElement);
        return newElement;
    }

    private <T> String determineRootName(Class<T> clasz) {
        XmlRootElement annotation = clasz.getAnnotation(XmlRootElement.class);
        String name = annotation != null
                && annotation.name() != null
                && !annotation.name().equals(DEFAULT_NAME)
                ? annotation.name()
                : clasz.getSimpleName();
        return name;
    }

    <T> T safelyCreateDecoratee(Class<T> clasz) {
        if (Integer.class.equals(clasz)) {
            return (T) new Integer(0);
        }
        if (clasz.isEnum()) {
            return null;
        }
        try {
            return clasz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException
                | SecurityException
                | InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException ex) {
            Logger.getLogger(AnnotationCreator.class.getName()).log(Level.SEVERE, null, ex);
            throw new CannotCreateInstance();
        }
    }

    private <T, P, C> void reflectOnElement(Field field, Class<C> clasz, XMLElement<T, P> currentElement) {
        if (field.isAnnotationPresent(XmlElementWrapper.class)) {
            XmlElementWrapper wrapperAnnotation = field.getAnnotation(XmlElementWrapper.class);
            String wrapperTag = wrapperAnnotation.name() != null
                    && !wrapperAnnotation.name().equals(DEFAULT_NAME)
                    ? wrapperAnnotation.name()
                    : field.getName();

            XMLElement<List, T> wrapperElement = element(wrapperTag, List.class, LinkedList::new, (parent, list) -> safeAssign(parent, list, field));
            currentElement.addElement(wrapperElement);
            createElement(field, findGenericType(field), wrapperElement, List::add);
        } else {
            createElement(field, clasz, currentElement, null);
        }
    }

    private Class findGenericType(Field field) {
        final Type genericType = field.getGenericType();
        Class findit = null;
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) genericType;

            Type innerType = Arrays.stream(pType.getActualTypeArguments()).findFirst()
                    .orElseThrow();

            try {
                findit = Class.forName(innerType.getTypeName());
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(AnnotationCreator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return findit;
    }

    private <C, T, P> void createElement(Field field, Class<C> clasz, XMLElement<T, P> newElement, BiConsumer<T, C> whenParsed_) {
        String tag = determineElementName(field);

        BiConsumer<T, C> whenParsed = whenParsed_ == null
                ? (parent, child) -> safeAssign(parent, child, field)
                : whenParsed_;

        XMLElement<C, T> subElement = element(
                tag,
                clasz,
                () -> safelyCreateDecoratee(clasz),
                whenParsed
        );

        if (!(newElement.getClasz().equals(subElement.getClasz()) && Objects.equals(tag, newElement.getTag()))) {
            newElement.addElement(subElement);
            addFieldElementsAndAttributes(clasz, subElement);
        } else {
            newElement.addElement((XMLElement<C, T>) newElement);
        }
    }

    private String determineElementName(Field field) {
        XmlElement elementAnnotations = field.getAnnotation(XmlElement.class);
        String tag = elementAnnotations != null
                && elementAnnotations.name() != null
                && !elementAnnotations.name().equals(DEFAULT_NAME)
                ? elementAnnotations.name()
                : field.getName();
        return tag;
    }

    private <C, P> void addFieldElementsAndAttributes(Class<C> clasz, XMLElement<C, P> subElement) throws AssertionError {
        if (terminalClasses.contains(clasz) || clasz.isEnum()) {
            return;
        }
        for (Field nField : Reflection.fieldsBeforeObjectClass(clasz)) {
            if (nField.isAnnotationPresent(XmlElement.class)
                    || nField.isAnnotationPresent(XmlElements.class)
                    || nField.isAnnotationPresent(XmlElementWrapper.class)) {
                reflectOnElement(nField, nField.getType(), subElement);
            } else if (nField.isAnnotationPresent(XmlAttribute.class)) {
                Class<?> fieldType = determineFieldType(nField);
                assignAttribute(nField, fieldType, subElement);
            } else {
                Class<?> fieldType = determineFieldType(nField);
                reflectOnElement(nField, fieldType, subElement);
                try {
                    assignAttribute(nField, fieldType, subElement);
                } catch (AttributeMapperNotFound e) {
                    /**
                     * In this case it is a rather complex type.
                     */
                }
            }
        }
    }

    private Class<?> determineFieldType(Field nField) throws AssertionError {
        Class<?> fieldType = nField.getType();
        if (fieldType.isPrimitive()) {
            switch (fieldType.getSimpleName()) {
                case "int":
                    fieldType = Integer.class;
                    break;
                case "double":
                    fieldType = Double.class;
                    break;
                case "float":
                    fieldType = Float.class;
                    break;
                case "boolean":
                    fieldType = Boolean.class;
                    break;
                case "short":
                    fieldType = Short.class;
                    break;

                default:
                    throw new AssertionError();
            }
        }
        return fieldType;
    }

    private <T, C> void safeAssign(T parent, C child, Field field) {
        try {
            field.setAccessible(true);
            if (!field.getType().isPrimitive()) {
                field.set(parent, field.getType().cast(child));
            } else {
                field.set(parent, child);
            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(AnnotationCreator.class.getName()).log(Level.SEVERE, null, ex);
            throw new FieldNotAccessible();
        }
    }

    private <T, P, C> void assignAttribute(Field field, Class<C> clasz, XMLElement<T, P> newElement) {

        String tag = determineAttributeName(field);

        XMLAttribute<C, T> newAttribute
                = attribute(
                        tag,
                        clasz,
                        mapperCreator.getMapperOrThrow(clasz),
                        (parent, child) -> safeAssign(parent, child, field)
                );

        newElement.addAttribute(newAttribute);
    }

    private String determineAttributeName(Field field) {
        XmlAttribute attributeAnnotations = field.getAnnotation(XmlAttribute.class);
        String tag = attributeAnnotations != null
                && attributeAnnotations.name() != null
                && !attributeAnnotations.name().equals(DEFAULT_NAME)
                ? attributeAnnotations.name()
                : field.getName();
        return tag;
    }

}
