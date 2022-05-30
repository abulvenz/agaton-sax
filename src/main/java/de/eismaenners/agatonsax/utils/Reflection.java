package de.eismaenners.agatonsax.utils;

import java.lang.reflect.Field;
import java.util.*;

public class Reflection {

    public static <T> List<Field> fieldsBeforeObjectClass(Class<T> clz) {
        if (clz == null) {
            Thread.dumpStack();
        }
        
        List<Field> fields = new LinkedList<>();
        Class<?> clazz = clz;
        while (clazz!= null && !Object.class.equals(clazz)) {
            Arrays.stream(clazz.getDeclaredFields())
                    .forEach(fields::add);
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
}
