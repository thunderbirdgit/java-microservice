package com.openease.common.manager.base.model;

import java.io.Serializable;
import java.util.Locale;

import static com.openease.common.util.JsonUtils.toJson;

/**
 * Base manager model
 *
 * @author Alan Czajkowski
 */
public abstract class BaseManagerModel implements Serializable {

  public static final Locale DEFAULT_LOCALE = Locale.US;

  /**
   * Override in sub-class
   */
  protected Class<?> getMixInClass() {
    return getClass();
  }

  /**
   * Override in sub-class
   */
  protected Class<?> getAttributeClassWithMixIn() {
    return null;
  }

  @Override
  public String toString() {
    return toJson(this, true);
  }

  public String toStringUsingMixIn() {
    if (getAttributeClassWithMixIn() == null) {
      return toJson(this, true, getMixInClass());
    } else {
      return toJson(this, true, getAttributeClassWithMixIn(), getAttributeClassWithMixIn().getDeclaredClasses()[0]);
    }
  }

}
