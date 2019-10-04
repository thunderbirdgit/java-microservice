package com.openease.common.web.api.base.model.response;

import com.openease.common.web.api.base.exception.ApiException;
import com.openease.common.web.api.base.model.Status;
import org.springframework.http.HttpStatus;

import static com.openease.common.web.mvc.base.exception.MvcException.DEFAULT_ERROR_HTTP_STATUS;

/**
 * Error API response
 *
 * @author Alan Czajkowski
 */
public class ErrorApiResponse extends BaseApiResponse<ErrorApiResponse> {

  public ErrorApiResponse() {
    this(DEFAULT_ERROR_HTTP_STATUS);
  }

  public ErrorApiResponse(HttpStatus httpStatus) {
    super(httpStatus);
  }

  public ErrorApiResponse(Status status) {
    super(status);
  }

  public ErrorApiResponse(ApiException ae) {
    this(new Status(ae.getHttpStatus().value(), ae.getMessage()));
  }

}
