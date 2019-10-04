package com.openease.common.util;

import org.junit.jupiter.api.Test;

import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResourceUtilsTest {

  private static final String TEST_RESOURCE_PATH = "/com/openease/common/openease-logo-ascii.txt";

  private ResourceUtilsTest() {
    // not publicly instantiable
  }

  @Test
  public void testGetPath() {
    String expected = TEST_RESOURCE_PATH;

    String actual = ResourceUtils.getPath(TEST_RESOURCE_PATH);
    if (endsWith(actual, TEST_RESOURCE_PATH)) {
      actual = TEST_RESOURCE_PATH;
    }

    assertEquals(expected, actual, ResourceUtils.class.getSimpleName() + ".getPath(resourcePath) failed");
  }

  @Test
  public void testReadResourceIntoString() {
    String expected = "Ideas To Put You At Ease";

    String actual = ResourceUtils.readResourceIntoString(TEST_RESOURCE_PATH).trim();
    if (contains(actual, expected)) {
      actual = expected;
    }

    assertEquals(actual, expected, ResourceUtils.class.getSimpleName() + ".readResourceIntoString(resourcePath) failed");
  }

}
