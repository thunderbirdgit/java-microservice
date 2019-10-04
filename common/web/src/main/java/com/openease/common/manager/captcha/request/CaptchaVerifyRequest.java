package com.openease.common.manager.captcha.request;

import com.openease.common.manager.base.model.BaseManagerModel;

import javax.validation.constraints.NotBlank;

import static com.openease.common.manager.lang.MessageKeys.VALIDATION_CAPTCHAVERIFYREQUEST_USERIP_NOTBLANK;
import static com.openease.common.manager.lang.MessageKeys.VALIDATION_CAPTCHAVERIFYREQUEST_USERTOKEN_NOTBLANK;

/**
 * Captcha: Verify request
 *
 * @author Alan Czajkowski
 */
public class CaptchaVerifyRequest extends BaseManagerModel {

  @NotBlank(message = "{" + VALIDATION_CAPTCHAVERIFYREQUEST_USERIP_NOTBLANK + "}")
  private String userIp;

  @NotBlank(message = "{" + VALIDATION_CAPTCHAVERIFYREQUEST_USERTOKEN_NOTBLANK + "}")
  private String userToken;

  public String getUserIp() {
    return userIp;
  }

  public CaptchaVerifyRequest setUserIp(String userIp) {
    this.userIp = userIp;
    return this;
  }

  public String getUserToken() {
    return userToken;
  }

  public CaptchaVerifyRequest setUserToken(String userToken) {
    this.userToken = userToken;
    return this;
  }

}
