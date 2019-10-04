package com.openease.common.web.util;

import org.springframework.web.bind.annotation.RequestMapping;

import static org.apache.commons.lang3.ArrayUtils.isEmpty;

/**
 * MVC utilities
 *
 * @author Alan Czajkowski
 */
public final class MvcUtils {

  private MvcUtils() {
    // not publicly instantiable
  }

  public static String getPath(Class<?> controllerClass) {
    return getPaths(controllerClass)[0];
  }

  public static String[] getPaths(Class<?> controllerClass) {
    RequestMapping requestMapping = controllerClass.getAnnotation(RequestMapping.class);
    String[] paths = requestMapping.value();
    // if "value" attribute is not used then check the (alias) "path" attribute
    if (isEmpty(paths)) {
      paths = requestMapping.path();
    }
    return paths;
  }

}
