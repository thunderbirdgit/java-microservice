package com.openease.common.web.api.base.model.response;

import com.openease.common.manager.base.model.BaseManagerModel;
import com.openease.common.web.api.base.model.Status;
import org.springframework.http.HttpStatus;

/**
 * Base API response
 *
 * @author Alan Czajkowski
 */
public abstract class BaseApiResponse<T extends BaseApiResponse> extends BaseManagerModel {

  private Status status;

  public BaseApiResponse() {
    this.status = new Status();
  }

  public BaseApiResponse(Status status) {
    this.status = status;
  }

  public BaseApiResponse(HttpStatus httpStatus) {
    this.status = new Status(httpStatus);
  }

  public Status getStatus() {
    return status;
  }

  @SuppressWarnings("unchecked")
  public T setStatus(Status status) {
    this.status = status;
    return (T) this;
  }

}
