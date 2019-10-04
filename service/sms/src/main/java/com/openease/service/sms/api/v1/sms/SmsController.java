package com.openease.service.sms.api.v1.sms;

import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.sms.request.SmsSendRequest;
import com.openease.common.web.api.base.BaseApiController;
import com.openease.common.web.api.base.exception.ApiException;
import com.openease.common.web.api.base.model.response.SuccessApiResponse;
import com.openease.service.sms.manager.sms.SmsManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import static com.openease.common.util.JsonUtils.toJson;
import static com.openease.common.web.api.ApiVersion.Constants.V1_CONTEXT;
import static com.openease.common.web.util.ApiUtils.createSuccessApiResponse;
import static com.openease.service.sms.api.v1.sms.SmsController.SMS_CONTEXT;
import static com.openease.service.sms.manager.sms.SmsManager.SMS;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * SMS controller
 *
 * @author Alan Czajkowski
 */
@Controller
@RequestMapping(path = V1_CONTEXT + SMS_CONTEXT, produces = APPLICATION_JSON_VALUE)
public class SmsController extends BaseApiController {

  private static final transient Logger LOG = LogManager.getLogger(SmsController.class);

  public static final String SMS_CONTEXT = "/" + SMS;

  @Autowired
  private SmsManager smsManager;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("paths: {}", () -> toJson(getPaths()));
    LOG.debug("Init finished");
  }

  /**
   * Call to send SMS
   *
   * @return {@link SuccessApiResponse}
   */
  @PostMapping(path = {"", "/"})
  public SuccessApiResponse send(@RequestBody @Valid SmsSendRequest request) {
    LOG.trace("request: {}", request);
    if (request == null) {
      throw new ApiException();
    }

    SuccessApiResponse response;

    try {
      smsManager.send(request);
      response = createSuccessApiResponse(CREATED);
    } catch (GeneralManagerException me) {
      LOG.warn(me::getMessage, me);
      throw new ApiException(me);
    }

    LOG.trace("response: {}", response);
    return response;
  }

}
