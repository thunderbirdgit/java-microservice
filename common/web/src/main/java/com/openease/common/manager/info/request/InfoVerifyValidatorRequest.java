package com.openease.common.manager.info.request;

import com.openease.common.manager.base.model.BaseManagerModel;

import javax.validation.constraints.NotNull;

/**
 * Info: Verify validator request
 *
 * @author Alan Czajkowski
 */
public class InfoVerifyValidatorRequest extends BaseManagerModel {

  public static final String VALIDATION_FAILURE_MESSAGE = "Attribute cannot be null";

  @NotNull(message = VALIDATION_FAILURE_MESSAGE)
  private String attribute;

  public String getAttribute() {
    return attribute;
  }

  public InfoVerifyValidatorRequest setAttribute(String attribute) {
    this.attribute = attribute;
    return this;
  }

}
