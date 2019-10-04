package com.openease.service.www.manager.email;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.openease.common.manager.email.RemoteEmailManager;
import com.openease.common.manager.email.request.EmailSendRequest;
import com.openease.common.manager.email.request.RemoteEmailSendRequest;
import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.template.TemplateManager;
import com.openease.common.web.util.RestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.gargoylesoftware.htmlunit.HttpMethod.POST;
import static com.openease.common.Env.Constants.LOCAL;
import static com.openease.common.Env.Constants.NOT;
import static com.openease.common.manager.lang.MessageKeys.MANAGER_EMAIL_ERROR_GENERALFAILURE;
import static com.openease.common.manager.lang.MessageKeys.MANAGER_EMAIL_ERROR_REMOTEAPIFAILURE;
import static com.openease.common.manager.template.TemplateManager.TEMPLATE_MODEL_SUBJECT;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Default remote email manager
 *
 * @author Alan Czajkowski
 */
@Profile({NOT + LOCAL})
@Service
public class DefaultRemoteEmailManager implements RemoteEmailManager {

  private static final transient Logger LOG = LogManager.getLogger(DefaultRemoteEmailManager.class);

  @Value("${manager.remoteEmail.api.baseUrl}")
  private String remoteEmailApiBaseUrl;

  @Autowired
  @Qualifier("messageSource")
  private MessageSource messageSource;

  @Autowired
  private TemplateManager templateManager;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("remote email API base URL: {}", remoteEmailApiBaseUrl);
    LOG.debug("Init finished");
  }

  @Override
  public void send(RemoteEmailSendRequest request) throws GeneralManagerException {
    LOG.debug("request: {}", request);

    try {
      String subject = messageSource.getMessage(request.getTemplate().getTitleKey(), null, request.getLocale());
      // setting subject in template model is not necessary, but done for completeness
      request.getTemplateModel().put(TEMPLATE_MODEL_SUBJECT, subject);

      String htmlBody = templateManager.render(request.getTemplate(), request.getTemplateModel(), request.getLocale());

      EmailSendRequest emailSendRequest = new EmailSendRequest()
          .addToRecipients(request.getToRecipients())
          .setSubject(subject)
          .setHtmlBody(htmlBody)
          .addAttachments(request.getAttachments())
          .addInlineImages(request.getInlineImages());

      // call remote email API
      LOG.debug("API call: {} {} | Payload:{}{}", () -> POST, () -> remoteEmailApiBaseUrl, System::lineSeparator, () -> emailSendRequest);
      WebResponse webResponse = RestUtils.jsonRequest(POST, remoteEmailApiBaseUrl, emailSendRequest);

      // check response
      HttpStatus.Series httpStatusSeries = HttpStatus.Series.valueOf(webResponse.getStatusCode());
      switch (httpStatusSeries) {
        case SUCCESSFUL:
          LOG.debug("Call to remote email API succeeded");
          break;
        default:
          String errorPayload = webResponse.getContentAsString(UTF_8);
          LOG.error("Call to remote email API failed:{}{}", System::lineSeparator, () -> errorPayload);
          throw new GeneralManagerException(MANAGER_EMAIL_ERROR_REMOTEAPIFAILURE, "Call to remote email API failed");
      }
    } catch (Exception e) {
      LOG.error("Problem sending email", e);
      throw new GeneralManagerException(MANAGER_EMAIL_ERROR_GENERALFAILURE, e);
    }
  }

}
