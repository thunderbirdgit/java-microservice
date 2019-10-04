package com.openease.common.web.api.base.model.response;

import com.openease.common.web.api.base.model.Status;
import org.springframework.http.HttpStatus;

/**
 * Success API response
 *
 * @author Alan Czajkowski
 */
public class SuccessApiResponse<R> extends BaseApiResponse<SuccessApiResponse> {

  private R result;

  public SuccessApiResponse() {
    this.result = null;
  }

  public SuccessApiResponse(HttpStatus httpStatus) {
    super(httpStatus);
    this.result = null;
  }

  public SuccessApiResponse(R result, HttpStatus httpStatus) {
    super(httpStatus);
    this.result = result;
  }

  public SuccessApiResponse(R result, HttpStatus httpStatus, String statusMessage) {
    super(new Status(httpStatus, statusMessage));
    this.result = result;
  }

  public SuccessApiResponse(HttpStatus httpStatus, String statusMessage) {
    this(null, httpStatus, statusMessage);
  }

  public SuccessApiResponse(R result, Status status) {
    super(status);
    this.result = result;
  }

  public SuccessApiResponse(Status status) {
    this(null, status);
  }

  public SuccessApiResponse(R result) {
    this.result = result;
  }

  public R getResult() {
    return result;
  }

  public SuccessApiResponse<R> setResult(R result) {
    this.result = result;
    return this;
  }

}
