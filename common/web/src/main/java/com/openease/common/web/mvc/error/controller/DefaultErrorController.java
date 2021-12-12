package com.openease.common.web.mvc.error.controller;

import com.openease.common.web.mvc.base.BaseMvcController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import static com.openease.common.util.JsonUtils.toJson;
import static com.openease.common.web.mvc.base.exception.MvcException.DEFAULT_ERROR_HTTP_STATUS;
import static com.openease.common.web.mvc.error.controller.DefaultErrorController.ERROR_CONTEXT;
import static org.apache.commons.codec.CharEncoding.UTF_8;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

/**
 * Default error controller
 *
 * @author Alan Czajkowski
 */
@Controller
@RequestMapping(path = ERROR_CONTEXT)
public class DefaultErrorController extends BaseMvcController implements ErrorController {

  private static final transient Logger LOG = LogManager.getLogger(DefaultErrorController.class);

  public static final String ERROR = "error";
  public static final String ERROR_CONTEXT = "/" + ERROR;

  private static final String SERVLET_ERROR_CODE_KEY = "javax.servlet.error.status_code";
  private static final String SERVLET_ERROR_MESSAGE_KEY = "javax.servlet.error.message";
  private static final String SERVLET_ERROR_URI_KEY = "javax.servlet.error.request_uri";
  private static final String SERVLET_ERROR_EXCEPTION_KEY = "javax.servlet.error.exception";

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("paths: {}", () -> toJson(getPaths()));
    LOG.debug("Init finished");
  }

  @GetMapping(path = {"", "/"}, produces = TEXT_PLAIN_VALUE + ";charset=" + UTF_8)
  public ResponseEntity<String> handle(HttpServletRequest httpRequest) {
    // defaults
    int code = DEFAULT_ERROR_HTTP_STATUS.value();
    String message = DEFAULT_ERROR_HTTP_STATUS.getReasonPhrase();

    // get servlet error code
    final Integer servletErrorCode = (Integer) httpRequest.getAttribute(SERVLET_ERROR_CODE_KEY);
    LOG.debug("Servlet error code: {}", servletErrorCode);

    // get servlet error message
    String servletErrorMessage = (String) httpRequest.getAttribute(SERVLET_ERROR_MESSAGE_KEY);
    LOG.trace("Servlet error message: [{}]", servletErrorMessage);
    final Exception servletErrorException = (Exception) httpRequest.getAttribute(SERVLET_ERROR_EXCEPTION_KEY);
    servletErrorMessage = servletErrorException == null
        ? servletErrorMessage
        : servletErrorException.getMessage();
    LOG.trace("Servlet error message: [{}]", servletErrorMessage);
    servletErrorMessage = isEmpty(servletErrorMessage)
        ? HttpStatus.valueOf(servletErrorCode).getReasonPhrase()
        : servletErrorMessage;
    LOG.debug("Servlet error message: [{}]", servletErrorMessage);

    // if there is error data then set code and message
    if (servletErrorCode != null) {
      code = servletErrorCode;
      if (isNotBlank(servletErrorMessage)) {
        message = servletErrorMessage;
      }
      if (code == NOT_FOUND.value() && startsWith(message, "/")) {
        message = NOT_FOUND.getReasonPhrase() + ": " + message;
      }
    }

    // get servlet error request path
    final String servletErrorUri = (String) httpRequest.getAttribute(SERVLET_ERROR_URI_KEY);

    if (HttpStatus.Series.valueOf(code) == HttpStatus.Series.SERVER_ERROR) {
      LOG.error("HTTP {}: {}", code, message);
    }

    // return error response
//    ErrorApiResponse response = createErrorApiResponse(code, message, config.getMask5xxErrors());
//    response.getStatus().setAdditionalInfo(servletErrorUri);
//    LOG.debug("response: {}", response);

    //TODO: return nicely styled error page (ModelAndView)
    ResponseEntity<String> responseEntity = new ResponseEntity<>(message, HttpStatus.valueOf(code));
    LOG.debug("responseEntity: {}", () -> toJson(responseEntity, true));
    return responseEntity;
  }

}
