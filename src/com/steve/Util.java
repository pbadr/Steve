package com.steve;

import java.lang.reflect.Field;

public class Util {
    static final String worldsPath = "worlds/";

    public static void reflectSet(Object object, String fieldName, Object fieldValue) {
        // by sp00m, source: https://stackoverflow.com/a/14374995/13216113

        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, fieldValue);
                return;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
