package com.openease.service.www.manager.sms;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.sms.RemoteSmsManager;
import com.openease.common.manager.sms.request.RemoteSmsSendRequest;
import com.openease.common.manager.sms.request.SmsSendRequest;
import com.openease.common.manager.template.TemplateManager;
import com.openease.common.web.util.RestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.gargoylesoftware.htmlunit.HttpMethod.POST;
import static com.openease.common.Env.Constants.LOCAL;
import static com.openease.common.Env.Constants.NOT;
import static com.openease.common.manager.lang.MessageKeys.MANAGER_SMS_ERROR_GENERALFAILURE;
import static com.openease.common.manager.lang.MessageKeys.MANAGER_SMS_ERROR_REMOTEAPIFAILURE;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Default remote SMS manager
 *
 * @author Alan Czajkowski
 */
@Profile({NOT + LOCAL})
@Service
public class DefaultRemoteSmsManager implements RemoteSmsManager {

  private static final transient Logger LOG = LogManager.getLogger(DefaultRemoteSmsManager.class);

  @Value("${manager.remoteSms.api.baseUrl}")
  private String remoteSmsApiBaseUrl;

  @Autowired
  private TemplateManager templateManager;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("remote SMS API base URL: {}", remoteSmsApiBaseUrl);
    LOG.debug("Init finished");
  }

  @Override
  public void send(RemoteSmsSendRequest request) throws GeneralManagerException {
    LOG.debug("request: {}", request);

    try {
      String message = templateManager.render(request.getTemplate(), request.getTemplateModel(), request.getLocale());

      SmsSendRequest smsSendRequest = new SmsSendRequest()
          .setRecipientPhoneNumber(request.getRecipientPhoneNumber())
          .setMessage(message);

      // call remote SMS API
      LOG.debug("API call: {} {} | Payload:{}{}", () -> POST, () -> remoteSmsApiBaseUrl, System::lineSeparator, () -> smsSendRequest);
      WebResponse webResponse = RestUtils.jsonRequest(POST, remoteSmsApiBaseUrl, smsSendRequest);

      // check response
      HttpStatus.Series httpStatusSeries = HttpStatus.Series.valueOf(webResponse.getStatusCode());
      switch (httpStatusSeries) {
        case SUCCESSFUL:
          LOG.debug("Call to remote SMS API succeeded");
          break;
        default:
          String errorPayload = webResponse.getContentAsString(UTF_8);
          LOG.error("Call to remote SMS API failed:{}{}", System::lineSeparator, () -> errorPayload);
          throw new GeneralManagerException(MANAGER_SMS_ERROR_REMOTEAPIFAILURE, "Call to remote SMS API failed");
      }
    } catch (Exception e) {
      LOG.error("Problem sending SMS", e);
      throw new GeneralManagerException(MANAGER_SMS_ERROR_GENERALFAILURE, e);
    }
  }

}
