package com.openease.common.web.api.base;

import com.openease.common.web.api.base.model.Status;
import com.openease.common.web.api.base.model.response.ErrorApiResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.openease.common.util.JsonUtils.toJson;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

/**
 * Global exception controller
 *
 * @author Alan Czajkowski
 */
@ControllerAdvice
public class GlobalExceptionController {

  private static final transient Logger LOG = LogManager.getLogger(GlobalExceptionController.class);

  /**
   * Handling a {@link HttpRequestMethodNotSupportedException} cannot be done in {@link BaseApiController}
   * so it is done inside this {@link ControllerAdvice} class instead
   *
   * @param exception    {@link HttpRequestMethodNotSupportedException}
   * @param httpRequest  {@link HttpServletRequest}
   * @param httpResponse {@link HttpServletResponse}
   *
   * @return {@link ModelAndView}
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<String> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    LOG.warn("{}: {}", () -> exception.getClass().getSimpleName(), exception::getMessage);

    Status status = new Status(METHOD_NOT_ALLOWED)
        .setAdditionalInfo(exception.getLocalizedMessage() + ": " + httpRequest.getRequestURI());

    // API response
    ErrorApiResponse response = new ErrorApiResponse(status);

    //TODO: return nicely styled error page (ModelAndView)
    String viewName = status.getCode().toString();
    ModelAndView mav = new ModelAndView(viewName);
    mav.addObject("json", toJson(response));

    // HTTP response
//    httpResponse.setStatus(status.getCode());
//    httpResponse.setContentType(APPLICATION_JSON_VALUE);

//    LOG.debug("response: {}", response);
//    return mav;

    ResponseEntity<String> responseEntity = createResponseEntity(METHOD_NOT_ALLOWED.getReasonPhrase(), METHOD_NOT_ALLOWED);
    LOG.debug("responseEntity: {}", () -> toJson(responseEntity, true));
    return responseEntity;
  }

  private ResponseEntity<String> createResponseEntity(String message, HttpStatus httpStatus) {
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.add(CONTENT_TYPE, TEXT_PLAIN_VALUE + ";charset=" + UTF_8);
    return new ResponseEntity<>(message, responseHeaders, httpStatus);
  }

}
