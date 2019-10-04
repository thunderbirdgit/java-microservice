package com.openease.common.manager.task.base.sms;

import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.sms.RemoteSmsManager;
import com.openease.common.manager.sms.request.RemoteSmsSendRequest;
import com.openease.common.manager.task.base.BaseTask;
import com.openease.common.util.spring.BeanLocator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.openease.common.util.JsonUtils.toJson;

/**
 * Base send SMS task
 *
 * @author Alan Czajkowski
 */
public abstract class BaseSendSmsTask extends BaseTask<Boolean> {

  private static final transient Logger LOG = LogManager.getLogger(BaseSendSmsTask.class);

  private RemoteSmsManager remoteSmsManager;

  public BaseSendSmsTask() {
    this.remoteSmsManager = BeanLocator.byClass(RemoteSmsManager.class);
  }

  @Override
  protected Boolean execute() {
    Boolean success = true;

    RemoteSmsSendRequest remoteSmsSendRequest = null;
    try {
      remoteSmsSendRequest = createRemoteSmsSendRequest();
      remoteSmsManager.send(remoteSmsSendRequest);
    } catch (GeneralManagerException me) {
      success = false;
      StringBuilder errorMessage = new StringBuilder()
          .append("[").append(id).append("] Error sending SMS");
      if (remoteSmsSendRequest != null) {
        errorMessage.append(", to: ")
            .append(toJson(remoteSmsSendRequest.getRecipientPhoneNumber()));
      }
      LOG.error(errorMessage::toString, me);
    }

    return success;
  }

  protected abstract RemoteSmsSendRequest createRemoteSmsSendRequest();

}
