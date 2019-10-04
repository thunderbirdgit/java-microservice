package com.openease.common.web.api.base.model;

import com.openease.common.manager.base.model.BaseManagerModel;

/**
 * API field error
 *
 * @author Alan Czajkowski
 */
public class ApiFieldError extends BaseManagerModel {

  private String objectName;

  private String field;

  private String message;

  private String rejectedValue;

  private String code;

  public String getField() {
    return field;
  }

  public ApiFieldError setField(String field) {
    this.field = field;
    return this;
  }

  public String getMessage() {
    return message;
  }

  public ApiFieldError setMessage(String message) {
    this.message = message;
    return this;
  }

  public String getObjectName() {
    return objectName;
  }

  public ApiFieldError setObjectName(String objectName) {
    this.objectName = objectName;
    return this;
  }

  public String getRejectedValue() {
    return rejectedValue;
  }

  public ApiFieldError setRejectedValue(String rejectedValue) {
    this.rejectedValue = rejectedValue;
    return this;
  }

  public String getCode() {
    return code;
  }

  public ApiFieldError setCode(String code) {
    this.code = code;
    return this;
  }

}
