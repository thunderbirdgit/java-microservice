package com.openease.service.sms.manager.sms;

import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.sms.request.SmsSendRequest;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.openease.common.manager.lang.MessageKeys.MANAGER_SMS_ERROR_GENERALFAILURE;
import static com.openease.common.util.JsonPasswordSerializer.MASK;

/**
 * Twilio SMS manager
 *
 * @author Alan Czajkowski
 * @see <a href="https://www.twilio.com/docs/api">Twilio | API</a>
 */
@Service
public class TwilioSmsManager implements SmsManager {

  private static final transient Logger LOG = LogManager.getLogger(TwilioSmsManager.class);

  @Value("${manager.sms.twilio.api.key.public}")
  private String apiPublicKey;

  @Value("${manager.sms.twilio.api.key.private}")
  private String apiPrivateKey;

  @Value("${manager.sms.twilio.sender.phone}")
  private String senderPhone;

  private PhoneNumber senderPhoneNumber;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");

    LOG.debug("Twilio API public key: {}", apiPublicKey);
    LOG.debug("Twilio API private key: {}", MASK);
    LOG.debug("Twilio sender phone: {}", senderPhone);

    Twilio.init(apiPublicKey, apiPrivateKey);
    senderPhoneNumber = new PhoneNumber(senderPhone);

    LOG.debug("Init finished");
  }

  @Override
  public void send(SmsSendRequest request) throws GeneralManagerException {
    LOG.debug("request: {}", request);

    try {
      PhoneNumber recipientPhoneNumber = new PhoneNumber(request.getRecipientPhoneNumber());
      Message message = Message.creator(
          recipientPhoneNumber,
          senderPhoneNumber,
          request.getMessage()
      ).create();
      LOG.debug("message.sid: {}", () -> message.getSid());
    } catch (Exception e) {
      LOG.error("Problem sending SMS", e);
      throw new GeneralManagerException(MANAGER_SMS_ERROR_GENERALFAILURE, "Problem sending SMS", e);
    }
  }

}
