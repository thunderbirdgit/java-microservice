package com.openease.common.web.api.information;

import com.openease.common.manager.info.InfoManager;
import com.openease.common.manager.info.request.InfoStatusRequest;
import com.openease.common.manager.info.request.InfoVerifyValidatorRequest;
import com.openease.common.manager.info.response.InfoStatusResponse;
import com.openease.common.web.api.base.BaseApiController;
import com.openease.common.web.api.base.exception.ApiException;
import com.openease.common.web.api.base.model.response.SuccessApiResponse;
import com.openease.common.web.util.ApiUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.Locale;

import static com.openease.common.manager.info.InfoManager.INFO;
import static com.openease.common.util.JsonUtils.toJson;
import static com.openease.common.web.api.information.InfoController.INFO_CONTEXT;
import static com.openease.common.web.lang.MessageKeys.INFO_CONTROLLER_ERROR4XX_MESSAGE;
import static com.openease.common.web.lang.MessageKeys.INFO_CONTROLLER_ERROR5XX_MESSAGE;
import static org.springframework.http.HttpStatus.I_AM_A_TEAPOT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Info controller
 *
 * @author Alan Czajkowski
 */
@Controller
@RequestMapping(path = INFO_CONTEXT, produces = APPLICATION_JSON_VALUE)
public class InfoController extends BaseApiController {

  private static final transient Logger LOG = LogManager.getLogger(InfoController.class);

  public static final String INFO_CONTEXT = "/" + INFO;

  @Autowired
  private InfoManager infoManager;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("paths: {}", () -> toJson(getPaths()));
    LOG.debug("Init finished");
  }

  /**
   * Call to get API status, version, etc.
   *
   * @return {@link SuccessApiResponse<InfoStatusResponse>}
   */
  @GetMapping(path = "/_status")
  public SuccessApiResponse<InfoStatusResponse> status() {
    Locale userLocale = LocaleContextHolder.getLocale();
    InfoStatusRequest infoStatusRequest = new InfoStatusRequest();
    infoStatusRequest.setLocale(userLocale);
    InfoStatusResponse infoStatusResponse = infoManager.status(infoStatusRequest);
    SuccessApiResponse<InfoStatusResponse> response = ApiUtils.createSuccessApiResponse(infoStatusResponse);

    LOG.debug("response: {}", response);
    return response;
  }

  /**
   * Call to verify API request validation works
   *
   * @param request {@link InfoVerifyValidatorRequest}
   *
   * @return {@link SuccessApiResponse}
   */
  @PostMapping(path = "/_verifyValidator")
  public SuccessApiResponse verifyValidator(@RequestBody @Valid InfoVerifyValidatorRequest request) {
    LOG.debug("request: {}", request);

    SuccessApiResponse response = ApiUtils.createSuccessApiResponse();

    LOG.debug("response: {}", response);
    return response;
  }

  /**
   * Call to verify URI encoding is set to UTF-8
   *
   * @param q querystring param to test UTF-8 encoding,
   *          example: _verifyUtf8?q=%C3%A4%C3%B6%C3%BC should produce "äöü" in the response
   *
   * @return {@link SuccessApiResponse<String>}
   */
  @GetMapping(path = "/_verifyUtf8")
  public SuccessApiResponse<String> verifyUtf8(@RequestParam(value = "q", required = false) String q) {
    LOG.debug("q: {}", q);

    SuccessApiResponse<String> response = ApiUtils.createSuccessApiResponse(q);

    LOG.debug("response: {}", response);
    return response;
  }

  /**
   * Call to return a 4xx error
   * - HTTP 418 (I-AM-A-TEAPOT) error
   *
   * @return {@link SuccessApiResponse} (never gets returned)
   *
   * @throws ApiException - HTTP 418 (I-AM-A-TEAPOT)
   */
  @GetMapping(path = "/_verifyError4xx")
  public SuccessApiResponse verifyError4xx() {
    throw new ApiException(I_AM_A_TEAPOT, INFO_CONTROLLER_ERROR4XX_MESSAGE);
  }

  /**
   * Call to return a 5xx error
   * - generic server error (good for testing whether production errors are masked appropriately)
   *
   * @return {@link SuccessApiResponse} (never gets returned)
   *
   * @throws RuntimeException - with sample error message
   */
  @GetMapping(path = "/_verifyError5xx")
  public SuccessApiResponse verifyError5xx() {
    Locale locale = LocaleContextHolder.getLocale();
    String sampleErrorMessage = messageSource.getMessage(INFO_CONTROLLER_ERROR5XX_MESSAGE, null, locale);
    RuntimeException exception = new RuntimeException(sampleErrorMessage);
    LOG.warn("{}: {}", () -> exception.getClass().getSimpleName(), exception::getMessage);
    throw exception;
  }

}
