package com.openease.common.util;

import com.openease.common.util.exception.GeneralUtilException;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Java utilities
 *
 * @author Alan Czajkowski
 */
public final class JavaUtils {

  private JavaUtils() {
    // not publicly instantiable
  }

  /**
   * Return a non-null {@link String}
   *
   * @param string a string (can be null)
   *
   * @return if null: {@link String} a non-null empty string, otherwise: itself
   */
  public static String notNull(String string) {
    return string == null
        ? ""
        : string;
  }

  /**
   * Return a non-null {@link List}
   *
   * @param list a list (can be null)
   *
   * @return if null: {@link List} a non-null immutable list, otherwise: itself
   */
  public static <T> List<T> notNull(List<T> list) {
    return list == null
        ? Collections.emptyList()
        : list;
  }

  /**
   * Return a non-null {@link Set}
   *
   * @param set a set (can be null)
   *
   * @return if null: {@link Set} a non-null immutable set, otherwise: itself
   */
  public static <T> Set<T> notNull(Set<T> set) {
    return set == null
        ? Collections.emptySet()
        : set;
  }

  /**
   * Return a non-null {@link Collection}
   *
   * @param collection a collection (List or Set, can be null)
   *
   * @return if null: {@link Collection} a non-null immutable collection, otherwise: itself
   */
  public static <T> Collection<T> notNull(Collection<T> collection) {
    if (collection instanceof List) {
      return notNull((List<T>) collection);
    } else if (collection instanceof Set) {
      return notNull((Set<T>) collection);
    }

    throw new GeneralUtilException("Not a List or Set");
  }

  /**
   * Return a non-null {@link Map}
   *
   * @param map a map (can be null)
   *
   * @return if null: {@link Map} a non-null immutable map, otherwise: itself
   */
  public static <K, V> Map<K, V> notNull(Map<K, V> map) {
    return map == null
        ? Collections.emptyMap()
        : map;
  }

  /**
   * Return a non-null array
   *
   * @param array an array (can be null)
   *
   * @return if null: a non-null array, otherwise: itself
   */
  @SuppressWarnings("unchecked")
  public static <T> T[] notNull(T... array) {
    return array == null
        ? (T[]) new Object[]{}
        : array;
  }

  public static String serialize(Serializable object) {
    return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
  }

  public static <T> T deserialize(String string, Class<T> clazz) {
    return clazz.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(string)));
  }

}
