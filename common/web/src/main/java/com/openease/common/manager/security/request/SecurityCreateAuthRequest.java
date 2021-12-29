package com.openease.common.manager.security.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.openease.common.manager.base.model.BaseManagerModel;
import com.openease.common.util.JsonPasswordSerializer;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import static com.openease.common.data.lang.MessageKeys.VALIDATION_EMAIL_INVALID;
import static com.openease.common.data.lang.MessageKeys.VALIDATION_EMAIL_NOTBLANK;
import static com.openease.common.data.lang.MessageKeys.VALIDATION_PASSWORD_NOTBLANK;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trim;

/**
 * Security: Create authentication (sign-in) request
 *
 * @author Alan Czajkowski
 */
public class SecurityCreateAuthRequest extends BaseManagerModel {

  @NotBlank(message = "{" + VALIDATION_EMAIL_NOTBLANK + "}")
  @Email(message = "{" + VALIDATION_EMAIL_INVALID + "}")
  private String username;

  @NotBlank(message = "{" + VALIDATION_PASSWORD_NOTBLANK + "}")
  private String password;

  private String captchaUserToken;

  public String getUsername() {
    return username;
  }

  public SecurityCreateAuthRequest setUsername(String username) {
    this.username = trim(lowerCase(username));
    return this;
  }

  public String getPassword() {
    return password;
  }

  public SecurityCreateAuthRequest setPassword(String password) {
    this.password = password;
    return this;
  }

  public String getCaptchaUserToken() {
    return captchaUserToken;
  }

  public SecurityCreateAuthRequest setCaptchaUserToken(String captchaUserToken) {
    this.captchaUserToken = captchaUserToken;
    return this;
  }

  private class MixIn extends SecurityCreateAuthRequest {

    @Override
    @JsonSerialize(using = JsonPasswordSerializer.class)
    public String getPassword() {
      return null;
    }

  }

  @Override
  protected Class<?> getMixInClass() {
    return SecurityCreateAuthRequest.MixIn.class;
  }

}
