package com.openease.common.manager.email;

import com.openease.common.manager.email.request.RemoteEmailSendRequest;
import com.openease.common.manager.exception.GeneralManagerException;

/**
 * Interface: Remote email manager
 *
 * @author Alan Czajkowski
 */
public interface RemoteEmailManager {

  void send(RemoteEmailSendRequest request) throws GeneralManagerException;

}
