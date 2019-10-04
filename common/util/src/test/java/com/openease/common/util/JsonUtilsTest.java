package com.openease.common.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonUtilsTest {

  private JsonUtilsTest() {
    // not publicly instantiable
  }

  public static class ExampleObject {
    private String attribute;

    public ExampleObject() {
      // do nothing
    }

    public ExampleObject(String attribute) {
      this.attribute = attribute;
    }

    public String getAttribute() {
      return attribute;
    }

    public void setAttribute(String attribute) {
      this.attribute = attribute;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof ExampleObject)) {
        return false;
      }
      ExampleObject that = (ExampleObject) o;
      return Objects.equals(getAttribute(), that.getAttribute());
    }

    @Override
    public int hashCode() {
      return Objects.hash(getAttribute());
    }
  }

  @Test
  public void testToJson() {
    String attribute = "test";
    ExampleObject object = new ExampleObject(attribute);
    String expected = "{\"attribute\":\"" + attribute + "\"}";

    String actual = JsonUtils.toJson(object);

    assertEquals(expected, actual, JsonUtils.class.getSimpleName() + ".toJson(object) failed");
  }

  @Test
  public void testToJsonArray() {
    String attribute = "test";
    List<ExampleObject> objectList = Arrays.asList(new ExampleObject(attribute), new ExampleObject(attribute));
    String expected = "[{\"attribute\":\"" + attribute + "\"},{\"attribute\":\"" + attribute + "\"}]";

    String actual = JsonUtils.toJson(objectList);

    assertEquals(expected, actual, JsonUtils.class.getSimpleName() + ".toJson(objectList) failed");
  }

  @Test
  public void testToJsonPretty() {
    String attribute = "test";
    ExampleObject object = new ExampleObject(attribute);
    String expected = "{" + lineSeparator() +
        "  \"attribute\" : \"" + attribute + "\"" + lineSeparator() +
        "}";

    String actual = JsonUtils.toJson(object, true);

    assertEquals(expected, actual, JsonUtils.class.getSimpleName() + ".toJson(object, prettyPrint) failed");
  }

  @Test
  public void testToObject() {
    String attribute = "test";
    String json = "{\"attribute\":\"" + attribute + "\"}";
    ExampleObject expected = new ExampleObject(attribute);

    ExampleObject actual = JsonUtils.toObject(json, ExampleObject.class);

    assertEquals(expected, actual, JsonUtils.class.getSimpleName() + ".toObject(json, class) failed");
  }

  @Test
  public void testToObjectList() {
    String attribute = "test";
    String json = "[{\"attribute\":\"" + attribute + "\"},{\"attribute\":\"" + attribute + "\"}]";
    List<ExampleObject> expected = Arrays.asList(new ExampleObject(attribute), new ExampleObject(attribute));

    List<ExampleObject> actual = JsonUtils.toObjectList(json, ExampleObject.class);

    assertEquals(expected, actual, JsonUtils.class.getSimpleName() + ".toObjectList(json, class) failed");
  }

}
