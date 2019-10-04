package com.openease.common.manager.payment;

import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.payment.request.RemotePaymentProcessRequest;

/**
 * Interface: Remote payment manager
 *
 * @author Alan Czajkowski
 */
public interface RemotePaymentManager {

  void process(RemotePaymentProcessRequest request) throws GeneralManagerException;

}
