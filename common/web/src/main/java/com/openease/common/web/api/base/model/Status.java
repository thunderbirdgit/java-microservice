package com.openease.common.web.api.base.model;

import com.openease.common.manager.base.model.BaseManagerModel;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.Objects;

import static org.springframework.http.HttpStatus.OK;

/**
 * Status
 *
 * @author Alan Czajkowski
 */
public class Status extends BaseManagerModel {

  public static final int SUBCODE_DEFAULT = 0;

  private Integer code;

  private Integer internalCode;

  private String message;

  private Object additionalInfo;

  private Date serverTimestamp;

  public Status() {
    this.code = OK.value();
    this.internalCode = SUBCODE_DEFAULT;
    this.message = OK.getReasonPhrase();
    this.additionalInfo = null;
    this.serverTimestamp = new Date();
  }

  public Status(Integer code, String message) {
    this();
    this.code = code != null
        ? code
        : OK.value();
    this.message = message;
  }

  public Status(HttpStatus httpStatus) {
    this();
    this.code = httpStatus.value();
    this.message = httpStatus.getReasonPhrase();
  }

  public Status(HttpStatus httpStatus, String message) {
    this();
    this.code = httpStatus.value();
    this.message = message;
  }

  public Status(Integer code, Integer internalCode, String message) {
    this();
    this.code = code != null
        ? code
        : OK.value();
    this.internalCode = internalCode;
    this.message = message;
  }

  public Status(HttpStatus httpStatus, Integer internalCode) {
    this();
    this.code = httpStatus.value();
    this.internalCode = internalCode;
    this.message = httpStatus.getReasonPhrase();
  }

  public Status(HttpStatus httpStatus, Integer internalCode, String message) {
    this();
    this.code = httpStatus.value();
    this.internalCode = internalCode;
    this.message = message;
  }

  public Integer getCode() {
    return code;
  }

  public Status setCode(Integer code) {
    this.code = code;
    return this;
  }

  public Integer getInternalCode() {
    return internalCode;
  }

  public Status setInternalCode(Integer internalCode) {
    this.internalCode = internalCode;
    return this;
  }

  public String getMessage() {
    return message;
  }

  public Status setMessage(String message) {
    this.message = message;
    return this;
  }

  public Object getAdditionalInfo() {
    return additionalInfo;
  }

  public Status setAdditionalInfo(Object additionalInfo) {
    this.additionalInfo = additionalInfo;
    return this;
  }

  public Date getServerTimestamp() {
    return serverTimestamp;
  }

  public Status setServerTimestamp(Date serverTimestamp) {
    this.serverTimestamp = serverTimestamp;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Status status = (Status) o;
    return getCode().equals(status.getCode())
        && getInternalCode().equals(status.getInternalCode())
        && getMessage().equals(status.getMessage());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getCode(), getInternalCode(), getMessage());
  }

}
