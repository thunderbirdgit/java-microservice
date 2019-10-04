package com.openease.common.web.util;

import com.openease.common.web.api.base.exception.ApiException;
import com.openease.common.web.api.base.model.Status;
import com.openease.common.web.api.base.model.response.ErrorApiResponse;
import com.openease.common.web.api.base.model.response.SuccessApiResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

import static com.openease.common.data.lang.MessageKeys.CRUD_ID_MISMATCH;
import static com.openease.common.web.mvc.base.exception.MvcException.DEFAULT_ERROR_HTTP_STATUS;
import static com.openease.common.web.util.HttpUtils.getIpAddress;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * API utilities
 *
 * @author Alan Czajkowski
 */
public final class ApiUtils {

  private static final transient Logger LOG = LogManager.getLogger(ApiUtils.class);

  private ApiUtils() {
    // not publicly instantiable
  }

  public static SuccessApiResponse createSuccessApiResponse() {
    return new SuccessApiResponse();
  }

  public static <T> SuccessApiResponse<T> createSuccessApiResponse(T result, MessageSource messageSource, String messageKey) {
    Locale locale = LocaleContextHolder.getLocale();
    String localizedMessage = messageSource.getMessage(messageKey, null, locale);

    Status status = new Status();
    status.setMessage(localizedMessage);

    return new SuccessApiResponse<>(result, status);
  }

  public static SuccessApiResponse createSuccessApiResponse(MessageSource messageSource, String messageKey) {
    return createSuccessApiResponse(null, messageSource, messageKey);
  }

  public static <T> SuccessApiResponse<T> createSuccessApiResponse(T result) {
    return new SuccessApiResponse<>(result);
  }

  public static <T> SuccessApiResponse<T> createSuccessApiResponse(T result, HttpStatus httpStatus) {
    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletResponse httpResponse = requestAttributes.getResponse();
    httpResponse.setStatus(httpStatus.value());
    return new SuccessApiResponse<>(result, httpStatus);
  }

  public static SuccessApiResponse createSuccessApiResponse(HttpStatus httpStatus) {
    return createSuccessApiResponse(null, httpStatus);
  }

  public static ErrorApiResponse createErrorApiResponse(Exception exception, HttpStatus httpStatus) {
    Status status = new Status(httpStatus)
        .setAdditionalInfo(exception.getLocalizedMessage());
    return new ErrorApiResponse(status);
  }

  public static ErrorApiResponse createErrorApiResponse(int code, String message, boolean mask5xxErrors) {
    HttpStatus httpStatus;
    try {
      httpStatus = HttpStatus.valueOf(code);
    } catch (IllegalArgumentException iae) {
      LOG.warn("HTTP status code {} is missing from HttpStatus enum", code);
      code = DEFAULT_ERROR_HTTP_STATUS.value();
      httpStatus = HttpStatus.valueOf(code);
    }

    if (httpStatus.is5xxServerError()) {
      LOG.trace("HTTP 5xx errors are masked: {}", mask5xxErrors);
      // if error is in 5xx series *and* error masking is turned on then mask the error
      if (mask5xxErrors) {
        message = httpStatus.getReasonPhrase();
      }
    }

    if (isEmpty(message)) {
      message = httpStatus.getReasonPhrase();
    }

    return new ErrorApiResponse(new Status(code, message));
  }

  public static void checkIdMatch(String idInUrl, String idInObject) {
    LOG.debug("idInUrl: {}", idInUrl);
    LOG.debug("idInObject: {}", idInObject);
    if (!StringUtils.equals(idInUrl, idInObject)) {
      HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
      String queryString = httpRequest.getQueryString();
      String uri = httpRequest.getRequestURI() + (isNotBlank(queryString) ? ("?" + queryString) : "");
      //TODO: do more than just log, send to event log?
      LOG.error("Hacking Detected: IP:[{}] {}:[{}] URL-ID:[{}] does not match Object-ID:[{}]", () -> getIpAddress(httpRequest), httpRequest::getMethod, () -> uri, () -> idInUrl, () -> idInObject);
      throw new ApiException(FORBIDDEN, CRUD_ID_MISMATCH);
    }
  }

}
