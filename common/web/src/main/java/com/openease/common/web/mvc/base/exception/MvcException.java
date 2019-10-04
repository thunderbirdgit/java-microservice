package com.openease.common.web.mvc.base.exception;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * MVC exception
 *
 * @author Alan Czajkowski
 */
public class MvcException extends RuntimeException {

  /**
   * @see <a href="https://en.wikipedia.org/wiki/List_of_HTTP_status_codes">Wikipedia | HTTP Status Codes</a>
   */
  public static final HttpStatus DEFAULT_ERROR_HTTP_STATUS = BAD_REQUEST;

  protected HttpStatus httpStatus;

  public MvcException() {
    super(DEFAULT_ERROR_HTTP_STATUS.getReasonPhrase());
    httpStatus = DEFAULT_ERROR_HTTP_STATUS;
  }

  public MvcException(String message) {
    super(message);
    httpStatus = DEFAULT_ERROR_HTTP_STATUS;
  }

  public MvcException(String message, Throwable cause) {
    super(message, cause);
    httpStatus = DEFAULT_ERROR_HTTP_STATUS;
  }

  public MvcException(HttpStatus httpStatus) {
    super(httpStatus.getReasonPhrase());
    this.httpStatus = httpStatus;
  }

  public MvcException(HttpStatus httpStatus, String message) {
    super(message);
    this.httpStatus = httpStatus;
  }

  public MvcException(HttpStatus httpStatus, String message, Throwable cause) {
    super(message, cause);
    this.httpStatus = httpStatus;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  public int getHttpStatusCode() {
    return httpStatus.value();
  }

}
