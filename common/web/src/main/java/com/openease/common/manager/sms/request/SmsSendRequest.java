package com.openease.common.manager.sms.request;

import com.openease.common.manager.base.model.BaseManagerModel;

/**
 * SMS: Send request
 *
 * @author Alan Czajkowski
 */
public class SmsSendRequest extends BaseManagerModel {

//TODO:  @NotBlank(message = "{" + VALIDATION_SMSSENDREQUEST_RECIPIENTPHONENUMBER_NOTBLANK + "}")
  private String recipientPhoneNumber;

//TODO:  @NotBlank(message = "{" + VALIDATION_SMSSENDREQUEST_MESSAGE_NOTBLANK + "}")
  private String message;

  public SmsSendRequest() {
    this.recipientPhoneNumber = null;
    this.message = null;
  }

  public String getRecipientPhoneNumber() {
    return recipientPhoneNumber;
  }

  public SmsSendRequest setRecipientPhoneNumber(String recipientPhoneNumber) {
    this.recipientPhoneNumber = recipientPhoneNumber;
    return this;
  }

  public String getMessage() {
    return message;
  }

  public SmsSendRequest setMessage(String message) {
    this.message = message;
    return this;
  }

}
