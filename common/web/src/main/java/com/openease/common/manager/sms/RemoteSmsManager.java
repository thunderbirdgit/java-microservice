package com.openease.common.manager.sms;

import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.sms.request.RemoteSmsSendRequest;

/**
 * Interface: Remote SMS manager
 *
 * @author Alan Czajkowski
 */
public interface RemoteSmsManager {

  void send(RemoteSmsSendRequest request) throws GeneralManagerException;

}
