package com.openease.common.manager.lang;

/**
 * Message keys
 * location: classpath:/com/openease/common/manager/lang/common-manager-messages.properties
 *
 * @author Alan Czajkowski
 */
public class MessageKeys {

  /**
   * General error messages
   */
  public static final String ERROR_METHODFAILURE =
      "error.methodFailure";
  public static final String ERROR_REMOTESERVICE_NOTAVAILABLE =
      "error.remoteService.notAvailable";

  /**
   * Validation messages for {@link com.openease.common.manager.email.request.EmailContact}
   */
  public static final String VALIDATION_EMAILRECIPIENT_EMAIL_NOTBLANK =
      "validation.emailRecipient.email.notBlank";
  public static final String VALIDATION_EMAILRECIPIENT_EMAIL_EMAIL =
      "validation.emailRecipient.email.email";

  /**
   * Validation messages for {@link com.openease.common.manager.email.request.EmailAttachment}
   */
  public static final String VALIDATION_EMAILATTACHMENT_FILENAME_NOTBLANK =
      "validation.emailAttachment.fileName.notBlank";
  public static final String VALIDATION_EMAILATTACHMENT_MIMETYPE_NOTBLANK =
      "validation.emailAttachment.mimeType.notBlank";
  public static final String VALIDATION_EMAILATTACHMENT_DATA_NOTNULL =
      "validation.emailAttachment.data.notNull";

  /**
   * Validation messages for {@link com.openease.common.manager.email.request.EmailSendRequest}
   */
  public static final String VALIDATION_EMAILSENDREQUEST_TORECIPIENTS_NOTEMPTY =
      "validation.emailSendRequest.toRecipients.notEmpty";
  public static final String VALIDATION_EMAILSENDREQUEST_SUBJECT_NOTBLANK =
      "validation.emailSendRequest.subject.notBlank";
  public static final String VALIDATION_EMAILSENDREQUEST_HTMLBODY_NOTNULL =
      "validation.emailSendRequest.htmlBody.notNull";

  /**
   * Messages for {@link com.openease.common.manager.payment.request.PaymentProcessRequest}
   */
  public static final String VALIDATION_PAYMENTPROCESSREQUEST_DESCRIPTION_NOTBLANK =
      "validation.paymentProcessRequest.description.notBlank";
  public static final String VALIDATION_PAYMENTPROCESSREQUEST_AMOUNT_NOTNULL =
      "validation.paymentProcessRequest.amount.notNull";
  public static final String VALIDATION_PAYMENTPROCESSREQUEST_AMOUNT_POSITIVE =
      "validation.paymentProcessRequest.amount.positive";
  public static final String VALIDATION_PAYMENTPROCESSREQUEST_CURRENCY_NOTNULL =
      "validation.paymentProcessRequest.currency.notNull";
  public static final String VALIDATION_PAYMENTPROCESSREQUEST_BILLINGTOKEN_NOTBLANK =
      "validation.paymentProcessRequest.billingToken.notBlank";

  /**
   * Messages for {@link com.openease.common.manager.account.AccountManager}
   */
  public static final String MANAGER_ACCOUNT_DISABLED =
      "manager.account.disabled";
  public static final String MANAGER_ACCOUNT_LOCKED =
      "manager.account.locked";
  public static final String MANAGER_ACCOUNT_CREDENTIALS_INVALID =
      "manager.account.credentials.invalid";
  public static final String MANAGER_ACCOUNT_PASSWORDS_IDENTICAL =
      "manager.account.passwords.identical";
  public static final String MANAGER_ACCOUNT_USERNAME_UNAVAILABLE =
      "manager.account.username.unavailable";

  /**
   * Messages for {@link com.openease.common.manager.email.RemoteEmailManager}
   */
  public static final String MANAGER_EMAIL_ERROR_ADDRESSFORMAT =
      "manager.email.error.addressFormat";
  public static final String MANAGER_EMAIL_ERROR_GENERALFAILURE =
      "manager.email.error.generalFailure";
  public static final String MANAGER_EMAIL_ERROR_REMOTEAPIFAILURE =
      "manager.email.error.remoteApiFailure";

  /**
   * Validation messages for {@link com.openease.common.manager.email.request.RemoteEmailSendRequest}
   */
  public static final String VALIDATION_REMOTEEMAILSENDREQUEST_TORECIPIENTS_NOTEMPTY =
      "validation.remoteEmailSendRequest.toRecipients.notEmpty";
  public static final String VALIDATION_REMOTEEMAILSENDREQUEST_TEMPLATE_NOTNULL =
      "validation.remoteEmailSendRequest.template.notNull";

  /**
   * Messages for {@link com.openease.common.manager.template.TemplateManager.Template}
   */
  public static final String TEMPLATES_EMAIL_HTML_WELCOME_TITLE =
      "templates.email.html.welcome.title";
  public static final String TEMPLATES_EMAIL_HTML_VERIFYACCOUNT_TITLE =
      "templates.email.html.verifyAccount.title";
  public static final String TEMPLATES_EMAIL_HTML_RESETPASSWORD_TITLE =
      "templates.email.html.resetPassword.title";
  public static final String TEMPLATES_EMAIL_HTML_NOTIFYPASSWORDCHANGE_TITLE =
      "templates.email.html.notifyPasswordChange.title";
  public static final String TEMPLATES_EMAIL_HTML_NOTIFYUSERNAMECHANGE_TITLE =
      "templates.email.html.notifyUsernameChange.title";
  public static final String TEMPLATES_SMS_TEXT_NOTIFYPASSWORDCHANGE_TITLE =
      "templates.sms.text.notifyPasswordChange.title";
  public static final String TEMPLATES_SMS_TEXT_NOTIFYUSERNAMECHANGE_TITLE =
      "templates.sms.text.notifyUsernameChange.title";

  /**
   * Messages for {@link com.openease.common.manager.jwt.JwtManager}
   */
  public static final String MANAGER_JWT_ERROR_GENERALFAILURE =
      "manager.jwt.error.generalFailure";

  /**
   * Messages for {@link com.openease.common.manager.payment.RemotePaymentManager}
   */
  public static final String MANAGER_PAYMENT_ERROR_GENERALFAILURE =
      "manager.payment.error.generalFailure";
  public static final String MANAGER_PAYMENT_ERROR_REMOTEAPIFAILURE =
      "manager.payment.error.remoteApiFailure";

  /**
   * Messages for {@link com.openease.common.manager.payment.request.RemotePaymentProcessRequest}
   */
  public static final String VALIDATION_REMOTEPAYMENTPROCESSREQUEST_DESCRIPTION_NOTBLANK =
      "validation.remotePaymentProcessRequest.description.notBlank";
  public static final String VALIDATION_REMOTEPAYMENTPROCESSREQUEST_AMOUNT_NOTNULL =
      "validation.remotePaymentProcessRequest.amount.notNull";
  public static final String VALIDATION_REMOTEPAYMENTPROCESSREQUEST_AMOUNT_POSITIVE =
      "validation.remotePaymentProcessRequest.amount.positive";
  public static final String VALIDATION_REMOTEPAYMENTPROCESSREQUEST_CURRENCY_NOTNULL =
      "validation.remotePaymentProcessRequest.currency.notNull";
  public static final String VALIDATION_REMOTEPAYMENTPROCESSREQUEST_BILLINGTOKEN_NOTBLANK =
      "validation.remotePaymentProcessRequest.billingToken.notBlank";

  /**
   * Messages for {@link com.openease.common.manager.sms.RemoteSmsManager}
   */
  public static final String MANAGER_SMS_ERROR_GENERALFAILURE =
      "manager.sms.error.generalFailure";
  public static final String MANAGER_SMS_ERROR_REMOTEAPIFAILURE =
      "manager.sms.error.remoteApiFailure";

  /**
   * Validation messages for {@link com.openease.common.manager.captcha.request.CaptchaVerifyRequest}
   */
  public static final String VALIDATION_CAPTCHAVERIFYREQUEST_USERTOKEN_NOTBLANK =
      "validation.captchaVerifyRequest.userToken.notBlank";
  public static final String VALIDATION_CAPTCHAVERIFYREQUEST_USERIP_NOTBLANK =
      "validation.captchaVerifyRequest.userIp.notBlank";

}
