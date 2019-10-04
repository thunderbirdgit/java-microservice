package com.openease.common.web.mvc.base;

import com.openease.common.config.Config;
import com.openease.common.web.mvc.base.exception.MvcException;
import com.openease.common.web.util.MvcUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;

import static com.openease.common.util.JsonUtils.toJson;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

/**
 * Base MVC controller
 *
 * @author Alan Czajkowski
 */
public abstract class BaseMvcController {

  private static final transient Logger LOG = LogManager.getLogger(BaseMvcController.class);

  @Autowired
  protected Config config;

  @Autowired
  @Qualifier("messageSource")
  protected MessageSource messageSource;

  @ExceptionHandler(MvcException.class)
  protected ResponseEntity<String> handleMvcException(MvcException exception) {
    LOG.warn("{}: {}", () -> exception.getClass().getSimpleName(), exception::getMessage);
    //TODO: return nicely styled error page (ModelAndView)
    ResponseEntity<String> responseEntity = createResponseEntity(exception.getHttpStatus().getReasonPhrase(), exception.getHttpStatus());
    LOG.debug("responseEntity: {}", () -> toJson(responseEntity, true));
    return responseEntity;
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<String> handleException(Exception exception, HttpServletResponse httpResponse) {
    LOG.error(exception::getMessage, exception);
    //TODO: return nicely styled error page (ModelAndView)
    ResponseEntity<String> responseEntity = createResponseEntity(INTERNAL_SERVER_ERROR.getReasonPhrase(), INTERNAL_SERVER_ERROR);
    LOG.debug("responseEntity: {}", () -> toJson(responseEntity, true));
    return responseEntity;
  }

  private ResponseEntity<String> createResponseEntity(String message, HttpStatus httpStatus) {
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.add(CONTENT_TYPE, TEXT_PLAIN_VALUE + ";charset=" + UTF_8);
    return new ResponseEntity<>(message, responseHeaders, httpStatus);
  }

  public String getPath() {
    return MvcUtils.getPath(getClass());
  }

  public String[] getPaths() {
    return MvcUtils.getPaths(getClass());
  }

}
