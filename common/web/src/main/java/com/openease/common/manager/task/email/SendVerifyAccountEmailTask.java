package com.openease.common.manager.task.email;

import com.openease.common.data.model.account.Account;
import com.openease.common.manager.task.base.email.BaseSendTemplateEmailTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

import static com.openease.common.manager.template.TemplateManager.TEMPLATE_MODEL_FIRSTNAME;
import static com.openease.common.manager.template.TemplateManager.TEMPLATE_MODEL_LASTNAME;
import static com.openease.common.manager.template.TemplateManager.Template.Email_HTML_VerifyAccount;

/**
 * Task: Send verify-account email
 *
 * @author Alan Czajkowski
 */
public class SendVerifyAccountEmailTask extends BaseSendTemplateEmailTask {

  private static final transient Logger LOG = LogManager.getLogger(SendVerifyAccountEmailTask.class);

  private static final String TEMPLATE_MODEL_VERIFICATIONURL = "verificationUrl";

  public SendVerifyAccountEmailTask(Locale locale, Account account, String verificationUrl) {
    super(Email_HTML_VerifyAccount, locale, account);
    templateModel.put(TEMPLATE_MODEL_FIRSTNAME, account.getFirstName());
    templateModel.put(TEMPLATE_MODEL_LASTNAME, account.getLastName());
    templateModel.put(TEMPLATE_MODEL_VERIFICATIONURL, verificationUrl);
  }

  @Override
  protected void logStart() {
    LOG.info(this::getLogStartMessage);
  }

  @Override
  protected void logFinish() {
    LOG.info(this::getLogFinishMessage);
  }

}
