package com.openease.common.web.api.base;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.openease.common.config.Config;
import com.openease.common.data.model.account.Account;
import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.log.EventLogManager;
import com.openease.common.web.api.base.exception.ApiException;
import com.openease.common.web.api.base.model.ApiFieldError;
import com.openease.common.web.api.base.model.Status;
import com.openease.common.web.api.base.model.response.ErrorApiResponse;
import com.openease.common.web.util.MvcUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.openease.common.util.JavaUtils.notNull;
import static com.openease.common.web.lang.MessageKeys.CONTROLLER_API_BASE_FIELDERROR;
import static com.openease.common.web.lang.MessageKeys.CONTROLLER_API_BASE_FIELDERRORS;
import static com.openease.common.web.lang.MessageKeys.CONTROLLER_API_BASE_INVALIDJSON;
import static com.openease.common.web.lang.MessageKeys.CONTROLLER_API_BASE_MESSAGENOTREADABLE;
import static com.openease.common.web.util.ApiUtils.createErrorApiResponse;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.commons.lang3.StringUtils.substring;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Base API controller
 *
 * @author Alan Czajkowski
 */
@ResponseBody
public abstract class BaseApiController {

  private static final transient Logger LOG = LogManager.getLogger(BaseApiController.class);

  @Autowired
  protected Config config;

  @Autowired
  @Qualifier("messageSource")
  protected MessageSource messageSource;

//TODO:  @Autowired
//TODO:  protected CaptchaManager captchaManager;

  @Autowired
  protected EventLogManager eventLogManager;

  //TODO: Captcha
  protected void verifyCaptcha(String captchaUserToken, HttpServletRequest httpRequest) {
//    LOG.debug("Verify Captcha");
//    String ipAddress = HttpUtils.getIpAddress(httpRequest);
//    CaptchaVerifyRequest captchaVerifyRequest = new CaptchaVerifyRequest(ipAddress, captchaUserToken);
//    boolean verified = captchaManager.verify(captchaVerifyRequest);
//    LOG.debug("Captcha verification passed: {}", verified);
//    if (!verified) {
//      throw new ApiException(PRECONDITION_FAILED, MANAGER_CAPTCHA_INVALID);
//    }
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<ErrorApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
    LOG.warn(() -> exception.getClass().getSimpleName());

    HttpStatus httpStatus = BAD_REQUEST;
    Locale locale = LocaleContextHolder.getLocale();

    // API response
    ErrorApiResponse response = new ErrorApiResponse(httpStatus);
    handleBindingResult(exception.getBindingResult(), response, locale);
    ResponseEntity<ErrorApiResponse> responseEntity = new ResponseEntity<>(response, httpStatus);

    LOG.debug("response: {}", responseEntity::getBody);
    return responseEntity;
  }

  @ExceptionHandler(BindException.class)
  protected ResponseEntity<ErrorApiResponse> handleBindException(BindException exception) {
    LOG.warn(() -> exception.getClass().getSimpleName());

    HttpStatus httpStatus = BAD_REQUEST;
    Locale locale = LocaleContextHolder.getLocale();

    // API response
    ErrorApiResponse response = new ErrorApiResponse(httpStatus);
    handleBindingResult(exception.getBindingResult(), response, locale);
    ResponseEntity<ErrorApiResponse> responseEntity = new ResponseEntity<>(response, httpStatus);

    LOG.debug("response: {}", responseEntity::getBody);
    return responseEntity;
  }

  @ExceptionHandler(ConstraintViolationException.class)
  protected ResponseEntity<ErrorApiResponse> handleConstraintViolationException(ConstraintViolationException exception) {
    LOG.warn(() -> exception.getClass().getSimpleName());

    HttpStatus httpStatus = BAD_REQUEST;
    Locale locale = LocaleContextHolder.getLocale();

    // API response
    ErrorApiResponse response = new ErrorApiResponse(httpStatus);
    Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
    String localizedMessage = messageSource.getMessage(CONTROLLER_API_BASE_FIELDERRORS, null, locale);
    response.getStatus().setMessage(localizedMessage + " " + constraintViolations.size());
    List<ApiFieldError> apiFieldErrors = new ArrayList<>();
    for (ConstraintViolation constraintViolation : constraintViolations) {
      String field = constraintViolation.getPropertyPath().toString();
      ApiFieldError apiFieldError = new ApiFieldError()
          .setField(field);
      String constraintViolationMessage = notNull(trim(constraintViolation.getMessage()));
      if (startsWith(constraintViolationMessage, "{") && endsWith(constraintViolationMessage, "}")) {
        constraintViolationMessage = substring(constraintViolationMessage, 1, constraintViolationMessage.length() - 1);
        constraintViolationMessage = messageSource.getMessage(constraintViolationMessage, null, locale);
      }
      apiFieldError.setMessage(constraintViolationMessage);
      Object rejectedValue = constraintViolation.getInvalidValue();
      if (rejectedValue != null) {
        if (containsIgnoreCase(field, "password")) {
          apiFieldError.setRejectedValue("*".repeat(rejectedValue.toString().length()));
        } else {
          apiFieldError.setRejectedValue(rejectedValue.toString());
        }
      }
      apiFieldErrors.add(apiFieldError);
    }
    response.getStatus().setAdditionalInfo(apiFieldErrors);
    ResponseEntity<ErrorApiResponse> responseEntity = new ResponseEntity<>(response, httpStatus);

    LOG.debug("response: {}", responseEntity::getBody);
    return responseEntity;
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  protected ResponseEntity<ErrorApiResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
    LOG.warn(() -> exception.getClass().getSimpleName());

    HttpStatus httpStatus = BAD_REQUEST;
    Locale locale = LocaleContextHolder.getLocale();

    // API response
    ErrorApiResponse response = new ErrorApiResponse(httpStatus);
    Throwable rootCause = exception.getRootCause();
    if (rootCause instanceof JsonMappingException) {
      String localizedMessage = messageSource.getMessage(CONTROLLER_API_BASE_FIELDERROR, null, locale);
      response.getStatus().setMessage(localizedMessage);
      JsonMappingException jsonMappingException = (JsonMappingException) rootCause;
      ApiFieldError apiFieldError = new ApiFieldError()
          .setMessage(jsonMappingException.getLocalizedMessage());
      for (JsonMappingException.Reference reference : jsonMappingException.getPath()) {
        apiFieldError.setField(reference.getFieldName());
        if (reference.getFrom() != null) {
          apiFieldError.setObjectName(reference.getFrom().toString());
        }
      }
      response.getStatus().setAdditionalInfo(Collections.singletonList(apiFieldError));
    } else if (rootCause instanceof JsonParseException) {
      String localizedMessage = messageSource.getMessage(CONTROLLER_API_BASE_INVALIDJSON, null, locale);
      response.getStatus().setMessage(localizedMessage);
    } else {
      String localizedMessage = messageSource.getMessage(CONTROLLER_API_BASE_MESSAGENOTREADABLE, null, locale);
      response.getStatus().setMessage(localizedMessage);
      response.getStatus().setAdditionalInfo(exception.getLocalizedMessage());
    }
    ResponseEntity<ErrorApiResponse> responseEntity = new ResponseEntity<>(response, httpStatus);

    LOG.debug("response: {}", responseEntity::getBody);
    return responseEntity;
  }

  @ExceptionHandler(ServletRequestBindingException.class)
  protected ResponseEntity<ErrorApiResponse> handleServletRequestBindingException(ServletRequestBindingException exception) {
    LOG.warn("{}: {}", () -> exception.getClass().getSimpleName(), exception::getMessage);

    ResponseEntity<ErrorApiResponse> responseEntity = createResponseEntity(exception, NOT_FOUND);

    LOG.debug("response: {}", responseEntity::getBody);
    return responseEntity;
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  protected ResponseEntity<ErrorApiResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
    LOG.warn("{}: {}", () -> exception.getClass().getSimpleName(), exception::getMessage);

    ResponseEntity<ErrorApiResponse> responseEntity = createResponseEntity(exception, NOT_FOUND);

    LOG.debug("response: {}", responseEntity::getBody);
    return responseEntity;
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  protected ResponseEntity<ErrorApiResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
    LOG.warn("{}: {}", () -> exception.getClass().getSimpleName(), exception::getMessage);

    ResponseEntity<ErrorApiResponse> responseEntity = createResponseEntity(exception, BAD_REQUEST);

    LOG.debug("response: {}", responseEntity::getBody);
    return responseEntity;
  }

  @ExceptionHandler(AccessDeniedException.class)
  protected ResponseEntity<ErrorApiResponse> handleAccessDeniedException(AccessDeniedException exception) {
    LOG.warn("{}: {}", () -> exception.getClass().getSimpleName(), exception::getMessage);

    ResponseEntity<ErrorApiResponse> responseEntity = createResponseEntity(exception, UNAUTHORIZED);

    LOG.debug("response: {}", responseEntity::getBody);
    return responseEntity;
  }

  @ExceptionHandler(ApiException.class)
  protected ResponseEntity<ErrorApiResponse> handleApiException(ApiException exception) {
    LOG.warn("{}: {}", () -> exception.getClass().getSimpleName(), exception::getMessage);

    HttpStatus httpStatus = exception.getHttpStatus();
    Locale locale = LocaleContextHolder.getLocale();
    String localizedMessage = messageSource.getMessage(exception.getKey(), null, locale);
    Status status = new Status()
        .setCode(httpStatus.value())
        .setMessage(localizedMessage)
        .setAdditionalInfo(exception.getMessage());

    // API response
    ErrorApiResponse response = new ErrorApiResponse(status);
    ResponseEntity<ErrorApiResponse> responseEntity = new ResponseEntity<>(response, httpStatus);

    LOG.debug("response: {}", responseEntity::getBody);
    return responseEntity;
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ErrorApiResponse> handleException(Exception exception) {
    LOG.error(exception::getMessage, exception);

    HttpStatus httpStatus = INTERNAL_SERVER_ERROR;
    boolean mask5xxErrors = config.getMask5xxErrors();

    // API response
    ErrorApiResponse response = createErrorApiResponse(httpStatus.value(), exception.getMessage(), mask5xxErrors);
    ResponseEntity<ErrorApiResponse> responseEntity = new ResponseEntity<>(response, httpStatus);

    LOG.debug("response: {}", responseEntity::getBody);
    return responseEntity;
  }

  protected void createEventLog(HttpServletRequest httpRequest, String description, String rawData, Account account) {
    try {
      eventLogManager.create(httpRequest, description, rawData, account);
    } catch (GeneralManagerException me) {
      LOG.error(me::getMessage, me);
    }
  }

  public String getPath() {
    return MvcUtils.getPath(getClass());
  }

  public String[] getPaths() {
    return MvcUtils.getPaths(getClass());
  }

  private void handleBindingResult(BindingResult bindingResult, ErrorApiResponse response, Locale locale) {
    String localizedMessage = messageSource.getMessage(CONTROLLER_API_BASE_FIELDERRORS, null, locale);
    response.getStatus().setMessage(localizedMessage + " " + bindingResult.getErrorCount());
    List<ApiFieldError> apiFieldErrors = new ArrayList<>();
    for (FieldError fieldError : bindingResult.getFieldErrors()) {
      String field = fieldError.getField();
      ApiFieldError apiFieldError = new ApiFieldError()
          .setField(field)
          .setCode(fieldError.getCode())
          .setMessage(fieldError.getDefaultMessage())
          .setObjectName(fieldError.getObjectName());
      Object rejectedValue = fieldError.getRejectedValue();
      if (rejectedValue != null) {
        if (containsIgnoreCase(field, "password")) {
          apiFieldError.setRejectedValue("*".repeat(rejectedValue.toString().length()));
        } else {
          apiFieldError.setRejectedValue(rejectedValue.toString());
        }
      }
      apiFieldErrors.add(apiFieldError);
    }
    response.getStatus().setAdditionalInfo(apiFieldErrors);
  }

  private ResponseEntity<ErrorApiResponse> createResponseEntity(Exception exception, HttpStatus httpStatus) {
    ErrorApiResponse response = createErrorApiResponse(exception, httpStatus);
    return new ResponseEntity<>(response, httpStatus);
  }

}
