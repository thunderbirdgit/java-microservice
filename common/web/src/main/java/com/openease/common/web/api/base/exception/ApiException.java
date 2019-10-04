package com.openease.common.web.api.base.exception;

import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.web.mvc.base.exception.MvcException;
import org.springframework.http.HttpStatus;

import static com.openease.common.data.lang.MessageKeys.CRUD_NOTFOUND;
import static com.openease.common.data.lang.MessageKeys.CRUD_UPDATE_STALE;
import static com.openease.common.manager.lang.MessageKeys.ERROR_METHODFAILURE;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * API exception
 *
 * @author Alan Czajkowski
 */
public class ApiException extends MvcException {

  private final String key;

  public ApiException() {
    super();
    this.key = ERROR_METHODFAILURE;
  }

  public ApiException(String key) {
    super();
    this.key = key;
  }

  public ApiException(GeneralManagerException me) {
    super(me.getMessage(), me);
    key = me.getKey();
    switch (key) {
      case CRUD_NOTFOUND:
        httpStatus = NOT_FOUND;
        break;
      case CRUD_UPDATE_STALE:
        httpStatus = CONFLICT;
        break;
    }
  }

  public ApiException(HttpStatus httpStatus, GeneralManagerException me) {
    super(httpStatus, me.getMessage());
    key = me.getKey();
  }

  public ApiException(HttpStatus httpStatus, String key) {
    super(httpStatus);
    this.key = key;
  }

  public ApiException(HttpStatus httpStatus, String key, String message) {
    super(httpStatus, message);
    this.key = key;
  }

  public ApiException(HttpStatus httpStatus, String key, String message, Throwable cause) {
    super(httpStatus, message, cause);
    this.key = key;
  }

  public ApiException(String key, String message, Throwable cause) {
    super(message, cause);
    this.key = key;
  }

  public String getKey() {
    return key;
  }

}
