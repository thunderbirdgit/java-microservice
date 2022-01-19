package com.openease.common.manager.task.base.email;

import com.openease.common.data.model.account.Account;
import com.openease.common.manager.email.request.EmailAttachment;
import com.openease.common.manager.email.request.EmailContact;
import com.openease.common.manager.email.request.RemoteEmailSendRequest;
import com.openease.common.manager.template.TemplateManager;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.openease.common.manager.template.TemplateManager.TEMPLATE_MODEL_INLINE_LOGO_CID;
import static com.openease.common.util.JsonUtils.toJson;
import static com.openease.common.web.util.AssetUtils.STATIC_ASSET_CLASSPATH;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

/**
 * Base send template email task
 *
 * @author Alan Czajkowski
 */
public abstract class BaseSendTemplateEmailTask extends BaseSendEmailTask {

  private static final transient Logger LOG = LogManager.getLogger(BaseSendTemplateEmailTask.class);

  protected static final String INLINE_IMAGE_CLASSPATH = STATIC_ASSET_CLASSPATH + "/img/";
  protected static final String LOGO_FILE_NAME = "openease-logo-basic.png";
  protected static final String LOGO_FILE_CLASSPATH = INLINE_IMAGE_CLASSPATH + LOGO_FILE_NAME;
  protected static final String LOGO_FILE_MIMETYPE = IMAGE_PNG_VALUE;

  private TemplateManager.Template template;

  protected Map<String, Object> templateModel;

  private Locale locale;

  private Account account;

  public BaseSendTemplateEmailTask(TemplateManager.Template template, Locale locale, Account account) {
    super();
    this.template = template;
    this.templateModel = new HashMap<>();
    this.locale = locale;
    this.account = account;
  }

  @Override
  protected RemoteEmailSendRequest createRemoteEmailSendRequest() {
    LOG.debug("[{}] templateModel: {}", () -> id, () -> toJson(templateModel));

    RemoteEmailSendRequest remoteEmailSendRequest = new RemoteEmailSendRequest()
        .setTemplate(template)
        .setTemplateModel(templateModel)
        .setLocale(locale);

    String recipientEmailAddress = trim(account.getUsername());
    String firstName = account.getFirstName() != null
        ? account.getFirstName()
        : "";
    String lastName = account.getLastName() != null
        ? account.getLastName()
        : "";
    String recipientName = trim(firstName + " " + lastName);
    EmailContact recipient = new EmailContact()
        .setEmail(recipientEmailAddress)
        .setName(recipientName);

    remoteEmailSendRequest.getToRecipients().add(recipient);

    // include inline (embedded) email assets
    try {
      byte[] logoFileData = IOUtils.resourceToByteArray(LOGO_FILE_CLASSPATH);
      EmailAttachment inlineLogo = new EmailAttachment()
          .setFileName(LOGO_FILE_NAME)
          .setMimeType(LOGO_FILE_MIMETYPE)
          .setData(logoFileData);
      remoteEmailSendRequest.addInlineImage(inlineLogo);
      templateModel.put(TEMPLATE_MODEL_INLINE_LOGO_CID, LOGO_FILE_NAME);
    } catch (IOException ioe) {
      LOG.error(() -> ("[" + id + "] " + ioe.getMessage()), ioe);
    }

    return remoteEmailSendRequest;
  }

}
