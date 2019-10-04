package com.openease.service.payment.manager.payment;

import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.payment.request.PaymentProcessRequest;

/**
 * Interface: Payment manager
 *
 * @author Alan Czajkowski
 */
public interface PaymentManager {

  String PAYMENTS = "payments";

  void process(PaymentProcessRequest request) throws GeneralManagerException;

}
