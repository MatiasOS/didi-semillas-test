package com.atixlabs.semillasmiddleware.security.util;

import java.lang.reflect.Field;

public final class ObjectMapper {

  private ObjectMapper() {}

  @SuppressWarnings("unchecked")
  public static <T> T mergeObjects(T first, T second)
      throws IllegalAccessException, InstantiationException {
    Class<?> clazz = first.getClass();
    Field[] fields = clazz.getDeclaredFields();
    Object returnValue = clazz.newInstance();
    for (Field field : fields) {
      if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) continue;
      field.setAccessible(true);
      Object value1 = field.get(first);
      Object value2 = field.get(second);
      Object value = (value1 != null) ? value1 : value2;
      field.set(returnValue, value);
    }
    return (T) returnValue;
  }
}
