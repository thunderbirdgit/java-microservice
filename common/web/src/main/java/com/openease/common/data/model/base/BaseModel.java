package com.openease.common.data.model.base;

import java.io.Serializable;

import static com.openease.common.util.JsonUtils.toJson;

/**
 * Base model
 *
 * @author Alan Czajkowski
 */
public abstract class BaseModel<T extends BaseModel> implements Serializable {

  protected BaseModel() {
  }

  @Override
  public String toString() {
    return toJson(this, true);
  }

  public String toStringUsingMixIn() {
    return toJson(this, true, getMixInClass());
  }

  /**
   * Override in sub-class
   */
  protected Class<?> getMixInClass() {
    return getClass();
  }

}
