package com.openease.common.manager.task.base.email;

import com.openease.common.manager.email.RemoteEmailManager;
import com.openease.common.manager.email.request.RemoteEmailSendRequest;
import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.task.base.BaseTask;
import com.openease.common.util.spring.BeanLocator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.openease.common.util.JavaUtils.notNull;
import static com.openease.common.util.JsonUtils.toJson;

/**
 * Base send email task
 *
 * @author Alan Czajkowski
 */
public abstract class BaseSendEmailTask extends BaseTask<Boolean> {

  private static final transient Logger LOG = LogManager.getLogger(BaseSendEmailTask.class);

  private RemoteEmailManager remoteEmailManager;

  public BaseSendEmailTask() {
    this.remoteEmailManager = BeanLocator.byClass(RemoteEmailManager.class);
  }

  @Override
  protected Boolean execute() {
    Boolean success = true;

    RemoteEmailSendRequest remoteEmailSendRequest = null;
    try {
      remoteEmailSendRequest = createRemoteEmailSendRequest();
      remoteEmailManager.send(remoteEmailSendRequest);
    } catch (GeneralManagerException me) {
      success = false;
      StringBuilder errorMessage = new StringBuilder()
          .append("[").append(id).append("] Error sending email");
      if (remoteEmailSendRequest != null) {
        errorMessage.append(", to: ")
            .append(toJson(notNull(remoteEmailSendRequest.getToRecipients())));
      }
      LOG.error(errorMessage::toString, me);
    }

    return success;
  }

  protected abstract RemoteEmailSendRequest createRemoteEmailSendRequest();

}
