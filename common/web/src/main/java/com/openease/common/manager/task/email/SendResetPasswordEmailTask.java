package com.openease.common.manager.task.email;

import com.openease.common.data.model.account.Account;
import com.openease.common.manager.task.base.email.BaseSendTemplateEmailTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

import static com.openease.common.manager.template.TemplateManager.TEMPLATE_MODEL_FIRSTNAME;
import static com.openease.common.manager.template.TemplateManager.TEMPLATE_MODEL_LASTNAME;
import static com.openease.common.manager.template.TemplateManager.Template.Email_HTML_ResetPassword;

/**
 * Task: Send reset-password email
 *
 * @author Alan Czajkowski
 */
public class SendResetPasswordEmailTask extends BaseSendTemplateEmailTask {

  private static final transient Logger LOG = LogManager.getLogger(SendResetPasswordEmailTask.class);

  private static final String TEMPLATE_MODEL_PASSWORDRESETURL = "passwordResetUrl";

  public SendResetPasswordEmailTask(Locale locale, Account account, String passwordResetUrl) throws Exception {
    super(Email_HTML_ResetPassword, locale, account);
    templateModel.put(TEMPLATE_MODEL_FIRSTNAME, account.getFirstName());
    templateModel.put(TEMPLATE_MODEL_LASTNAME, account.getLastName());
    templateModel.put(TEMPLATE_MODEL_PASSWORDRESETURL, passwordResetUrl);
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
