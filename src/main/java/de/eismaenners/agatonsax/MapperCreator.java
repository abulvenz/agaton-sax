package de.eismaenners.agatonsax;

import de.eismaenners.agatonsax.exceptions.AttributeMapperNotFound;
import de.eismaenners.agatonsax.exceptions.WrongEnumType;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapperCreator {

    Map<Class<?>, Function<String, ?>> mappers = new HashMap<>();

    protected void addDefaultMappers() {
        addMapper(String.class, Function.identity());
        addMapper(Integer.class, Integer::decode);
        addMapper(Double.class, Double::parseDouble);
        addMapper(Float.class, Float::parseFloat);
        addMapper(Boolean.class, Boolean::parseBoolean);
        addMapper(Short.class, Short::parseShort);
    }

    public <C> void addMapper(Class<C> clasz, Function<String, C> mapper) {
        mappers.put(clasz, mapper);
    }

    protected <C> Function<String, C> createEnumAttribute(Class<C> clasz) {
        if (!clasz.isEnum()) {
            throw new ExpectedEnum(clasz);
        }
        Map<String, Object> createEnumMap = createEnumMap(clasz);
        return str -> Optional.ofNullable((C)createEnumMap.get(str))
                .orElseThrow(() -> new WrongEnumType(str, clasz));
    }

    @SuppressWarnings(value = {"rawtypes", "unchecked"})
    protected <C> Map<String, Object> createEnumMap(Class<C> type) {
        Map<String, Object> map = Arrays.stream(type.getFields())
                .filter(f -> f.isAnnotationPresent(XmlEnumValue.class))
                .collect(Collectors.toMap(f -> f.getAnnotation(XmlEnumValue.class).value(), f -> Enum.valueOf((Class<Enum>) type, f.getName())));
        if (map.isEmpty()) {
            map = Arrays.stream(type.getFields())
                    .collect(Collectors.toMap(f -> f.getName(), f -> Enum.valueOf((Class<Enum>) type, f.getName())));
        }
        return map;
    }

    public <C> Function<String, C> getMapperOrThrow(Class<C> clasz) {
        Function<String, C> mapper = getMapper(clasz);
        if (mapper == null) {
            throw new AttributeMapperNotFound(clasz);
        }
        return mapper;
    }

    public <C> Function<String, C> getMapper(Class<C> clasz) {
        Function<String, C> mapper = (Function<String, C>) mappers.get(clasz);
        if (clasz.isEnum() || clasz.isAnnotationPresent(XmlEnum.class)) {
            mapper = createEnumAttribute(clasz);
        }
        return mapper;
    }

}
