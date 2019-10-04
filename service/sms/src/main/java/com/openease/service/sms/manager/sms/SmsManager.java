package com.openease.service.sms.manager.sms;

import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.sms.request.SmsSendRequest;

/**
 * Interface: SMS manager
 *
 * @author Alan Czajkowski
 */
public interface SmsManager {

  String SMS = "sms";

  void send(SmsSendRequest request) throws GeneralManagerException;

}
