package com.openease.service.email.manager.email;

import com.openease.common.manager.email.request.EmailSendRequest;
import com.openease.common.manager.exception.GeneralManagerException;

/**
 * Interface: Email manager
 *
 * @author Alan Czajkowski
 */
public interface EmailManager {

  String EMAILS = "emails";

  void submit(EmailSendRequest request) throws GeneralManagerException;

  void send(EmailSendRequest request) throws GeneralManagerException;

  void send(String recipientAddress, String recipientName, String subject, String htmlBody) throws GeneralManagerException;

  void send(String recipientAddress, String subject, String htmlBody) throws GeneralManagerException;

}
