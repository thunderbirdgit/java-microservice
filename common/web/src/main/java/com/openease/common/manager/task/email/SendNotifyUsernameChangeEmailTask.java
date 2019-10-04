package com.openease.common.manager.task.email;

import com.openease.common.data.model.account.Account;
import com.openease.common.manager.task.base.email.BaseSendTemplateEmailTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

import static com.openease.common.manager.template.TemplateManager.TEMPLATE_MODEL_FIRSTNAME;
import static com.openease.common.manager.template.TemplateManager.TEMPLATE_MODEL_LASTNAME;
import static com.openease.common.manager.template.TemplateManager.Template.Email_HTML_NotifyUsernameChange;

/**
 * Task: Send notify-username-change email
 *
 * @author Alan Czajkowski
 */
public class SendNotifyUsernameChangeEmailTask extends BaseSendTemplateEmailTask {

  private static final transient Logger LOG = LogManager.getLogger(SendNotifyUsernameChangeEmailTask.class);

  private static final String TEMPLATE_MODEL_OLDEMAILADDRESS = "oldEmailAddress";
  private static final String TEMPLATE_MODEL_NEWEMAILADDRESS = "newEmailAddress";

  public SendNotifyUsernameChangeEmailTask(Locale locale, Account account, Account staleAccount) {
    super(Email_HTML_NotifyUsernameChange, locale, staleAccount);
    templateModel.put(TEMPLATE_MODEL_FIRSTNAME, account.getFirstName());
    templateModel.put(TEMPLATE_MODEL_LASTNAME, account.getLastName());
    templateModel.put(TEMPLATE_MODEL_OLDEMAILADDRESS, staleAccount.getUsername());
    templateModel.put(TEMPLATE_MODEL_NEWEMAILADDRESS, account.getUsername());
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
