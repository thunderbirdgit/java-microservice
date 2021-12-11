package com.openease.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.jayway.jsonpath.JsonPath;
import com.openease.common.util.exception.GeneralUtilException;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * JSON utilities
 *
 * @author Alan Czajkowski
 */
public final class JsonUtils {

  private static final transient Logger LOG = LogManager.getLogger(JsonUtils.class);

  private static final ObjectMapper JSON_OBJECT_MAPPER;

  /**
   * Initialize JSON object mapper
   *
   * @return {@link ObjectMapper}
   */
  private static ObjectMapper createObjectMapper() {
    return new ObjectMapper()
        .enable(ALLOW_UNQUOTED_FIELD_NAMES)
        .findAndRegisterModules();
  }

  /**
   * Constructor
   */
  static {
    JSON_OBJECT_MAPPER = createObjectMapper();
  }

  private JsonUtils() {
    // not publicly instantiable
  }

  /**
   * Serialize from {@link Object} to JSON
   *
   * @param object      object to serialize
   * @param prettyPrint whether to show the output in a human-readable format
   *
   * @return object serialized to JSON
   */
  public static String toJson(Object object, boolean prettyPrint) {
    return toJson(JSON_OBJECT_MAPPER, object, prettyPrint);
  }

  /**
   * Serialize from {@link Object} to JSON
   *
   * @param object object to serialize
   *
   * @return object serialized to JSON
   */
  public static String toJson(Object object) {
    return toJson(object, false);
  }

  /**
   * Serialize from {@link Object} to JSON using mix-in
   *
   * @param object      object to serialize
   * @param prettyPrint whether to show the output in a human-readable format
   * @param mixInClass  mix-in class to be used
   *
   * @return object serialized to JSON
   */
  public static String toJson(Object object, boolean prettyPrint, Class<?> mixInClass) {
    // init custom object mapper for mixin
    ObjectMapper jsonObjectMapper = createObjectMapper();
    jsonObjectMapper.addMixIn(object.getClass(), mixInClass);
    return toJson(jsonObjectMapper, object, prettyPrint);
  }

  /**
   * Serialize from {@link Object} to JSON using mix-in
   *
   * @param object              object to serialize
   * @param prettyPrint         whether to show the output in a human-readable format
   * @param attributeClass      class of the attribute (member variable) of object that requires a mix-in
   * @param attributeMixInClass mix-in class to be used for the class of the attribute (member variable) of object
   *
   * @return object serialized to JSON
   */
  public static String toJson(Object object, boolean prettyPrint, Class<?> attributeClass, Class<?> attributeMixInClass) {
    // init custom object mapper for mixin
    ObjectMapper jsonObjectMapper = createObjectMapper();
    jsonObjectMapper.addMixIn(attributeClass, attributeMixInClass);
    return toJson(jsonObjectMapper, object, prettyPrint);
  }

  /**
   * Serialize from {@link Object} to JSON using mix-in
   *
   * @param object     object to serialize
   * @param mixInClass mix-in class to be used
   *
   * @return object serialized to JSON
   */
  public static String toJson(Object object, Class<?> mixInClass) {
    return toJson(object, false, mixInClass);
  }

  /**
   * De-serialize from JSON to {@link Object}
   *
   * @param json  JSON string
   * @param clazz class to de-serialize to
   * @param <T>   type of class
   *
   * @return JSON de-serialized into object
   */
  public static <T> T toObject(String json, Class<T> clazz) {
    LOG.trace("Converting JSON:{}{}", System::lineSeparator, () -> json);

    T object;

    try {
      object = JSON_OBJECT_MAPPER.readValue(json, clazz);
    } catch (Exception e) {
      LOG.error(e::getMessage, e);
      throw new GeneralUtilException(e.getMessage(), e);
    }

    return object;
  }

  /**
   * De-serialize from {@link Reader} to {@link Object}
   *
   * @param reader reader
   * @param clazz  class to de-serialize to
   * @param <T>    type of class
   *
   * @return JSON de-serialized into object
   */
  public static <T> T toObject(Reader reader, Class<T> clazz) throws IOException {
    LOG.trace("Converting {}", Reader.class::getSimpleName);

    String json = IOUtils.toString(reader);
    reader.close();

    return toObject(json, clazz);
  }

  /**
   * De-serialize from JSON to a {@link List} of {@link Object}s
   *
   * @param json  JSON string
   * @param clazz class to de-serialize to
   * @param <T>   type of class
   *
   * @return JSON de-serialized into a list objects
   */
  public static <T> List<T> toObjectList(String json, Class<T> clazz) {
    LOG.trace("Converting JSON:{}{}", System::lineSeparator, () -> json);

    List<T> objectList;

    if (isNotBlank(json)) {
      try {
        CollectionType valueType = JSON_OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
        objectList = JSON_OBJECT_MAPPER.readValue(json, valueType);
      } catch (Exception e) {
        LOG.error(e::getMessage, e);
        throw new GeneralUtilException(e.getMessage(), e);
      }
    } else {
      objectList = new ArrayList<>();
    }

    return objectList;
  }

  /**
   * Traverse JSON object using JSON path
   *
   * @param json     JSON
   * @param jsonPath JSON path
   *
   * @return some object
   */
  public Object jsonPath(String json, String jsonPath) {
    return JsonPath.read(json, jsonPath);
  }

  /**
   * Traverse JSON object using JSON path
   *
   * @param json     JSON
   * @param jsonPath JSON path
   * @param clazz    class to convert to
   * @param <T>      type to return
   *
   * @return object of type T
   */
  @SuppressWarnings("unchecked")
  public <T> T jsonPath(String json, String jsonPath, Class<T> clazz) {
    return (T) JsonPath.read(json, jsonPath);
  }

  private static String toJson(ObjectMapper jsonObjectMapper, Object object, boolean prettyPrint) {
    String string;

    try {
      if (prettyPrint) {
        string = jsonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
      } else {
        string = jsonObjectMapper.writeValueAsString(object);
      }
    } catch (Exception e) {
      LOG.error(e::getMessage, e);
      throw new GeneralUtilException(e.getMessage(), e);
    }

    return string;
  }

}
