package com.openease.service.email.manager.email.task;

import com.openease.common.manager.email.request.EmailSendRequest;
import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.task.base.BaseTask;
import com.openease.common.util.spring.BeanLocator;
import com.openease.service.email.manager.email.EmailManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.openease.common.util.JavaUtils.notNull;
import static com.openease.common.util.JsonUtils.toJson;

/**
 * Send email task
 *
 * @author Alan Czajkowski
 */
public class SendEmailTask extends BaseTask<Boolean> {

  private static final transient Logger LOG = LogManager.getLogger(SendEmailTask.class);

  private EmailSendRequest emailSendRequest;

  private EmailManager emailManager;

  public SendEmailTask(EmailSendRequest emailSendRequest) throws Exception {
    this.emailSendRequest = emailSendRequest;
    this.emailManager = BeanLocator.byClass(EmailManager.class);
  }

  @Override
  protected Boolean execute() {
    Boolean success = true;

    try {
      emailManager.send(emailSendRequest);
    } catch (GeneralManagerException me) {
      success = false;
      StringBuilder errorMessage = new StringBuilder()
          .append("[").append(id).append("] Error sending email");
      if (emailSendRequest != null) {
        errorMessage.append(", to: ")
            .append(toJson(notNull(emailSendRequest.getToRecipients())));
      }
      LOG.error(errorMessage::toString, me);
    }

    return success;
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
