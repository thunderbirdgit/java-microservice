package com.openease.common.manager.account.request;

import com.openease.common.manager.base.model.BaseManagerModel;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import static com.openease.common.data.lang.MessageKeys.VALIDATION_EMAIL_INVALID;
import static com.openease.common.data.lang.MessageKeys.VALIDATION_EMAIL_NOTBLANK;
import static com.openease.common.manager.lang.MessageKeys.VALIDATION_CAPTCHAVERIFYREQUEST_USERTOKEN_NOTBLANK;

/**
 * Account: Send password reset code request
 *
 * @author Alan Czajkowski
 */
public class AccountSendPasswordResetCodeRequest extends BaseManagerModel {

  @NotBlank(message = "{" + VALIDATION_EMAIL_NOTBLANK + "}")
  @Email(message = "{" + VALIDATION_EMAIL_INVALID + "}")
  private String email;

//  @NotBlank(message = "{" + VALIDATION_CAPTCHAVERIFYREQUEST_USERTOKEN_NOTBLANK + "}")
  private String captchaUserToken;

  public String getEmail() {
    return email;
  }

  public AccountSendPasswordResetCodeRequest setEmail(String email) {
    this.email = email;
    return this;
  }

  public String getCaptchaUserToken() {
    return captchaUserToken;
  }

  public AccountSendPasswordResetCodeRequest setCaptchaUserToken(String captchaUserToken) {
    this.captchaUserToken = captchaUserToken;
    return this;
  }

}
