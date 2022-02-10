package com.openease.service.email.api.v1.emails;

import com.openease.common.manager.email.request.EmailSendRequest;
import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.web.api.base.BaseApiController;
import com.openease.common.web.api.base.exception.ApiException;
import com.openease.common.web.api.base.model.response.SuccessApiResponse;
import com.openease.service.email.manager.email.EmailManager;
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
import static com.openease.service.email.api.v1.emails.EmailsController.EMAILS_CONTEXT;
import static com.openease.service.email.manager.email.EmailManager.EMAILS;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Emails controller
 *
 * @author Alan Czajkowski
 */
@Controller
@RequestMapping(path = V1_CONTEXT + EMAILS_CONTEXT, produces = APPLICATION_JSON_VALUE)
public class EmailsController extends BaseApiController {

  private static final transient Logger LOG = LogManager.getLogger(EmailsController.class);

  public static final String EMAILS_CONTEXT = "/" + EMAILS;

  @Autowired
  private EmailManager emailManager;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("paths: {}", () -> toJson(getPaths()));
    LOG.debug("Init finished");
  }

  /**
   * Send email
   */
  @PostMapping(path = {"", "/"})
  public SuccessApiResponse send(@RequestBody @Valid EmailSendRequest request) {
    LOG.trace("request: {}", request);
    if (request == null) {
      throw new ApiException();
    }

    SuccessApiResponse response;

    try {
      emailManager.submit(request);
      response = createSuccessApiResponse(CREATED);
    } catch (GeneralManagerException me) {
      LOG.warn(me::getMessage, me);
      throw new ApiException(me);
    }

    LOG.trace("response: {}", response);
    return response;
  }

}
