package com.openease.common.manager.task.base.sms;

import com.openease.common.data.model.account.Account;
import com.openease.common.manager.sms.request.RemoteSmsSendRequest;
import com.openease.common.manager.template.TemplateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.openease.common.util.JsonUtils.toJson;

/**
 * Base send template SMS task
 *
 * @author Alan Czajkowski
 */
public abstract class BaseSendTemplateSmsTask extends BaseSendSmsTask {

  private static final transient Logger LOG = LogManager.getLogger(BaseSendTemplateSmsTask.class);

  private TemplateManager.Template template;

  protected Map<String, Object> templateModel;

  private Locale locale;

  private Account account;

  public BaseSendTemplateSmsTask(TemplateManager.Template template, Locale locale, Account account) {
    super();
    this.template = template;
    this.templateModel = new HashMap<>();
    this.locale = locale;
    this.account = account;
  }

  @Override
  protected RemoteSmsSendRequest createRemoteSmsSendRequest() {
    LOG.debug("[{}] templateModel: {}", () -> id, () -> toJson(templateModel));

    RemoteSmsSendRequest remoteSmsSendRequest = new RemoteSmsSendRequest()
        .setTemplate(template)
        .setTemplateModel(templateModel)
        .setLocale(locale);

//TODO:    remoteSmsSendRequest.setRecipientPhoneNumber(account.get);

    return remoteSmsSendRequest;
  }

}
