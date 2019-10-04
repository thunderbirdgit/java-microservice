package com.openease.common.manager.account.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.openease.common.manager.base.model.BaseManagerModel;
import com.openease.common.util.JsonPasswordSerializer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.openease.common.data.lang.MessageKeys.VALIDATION_ACCOUNT_PASSWORDRESETCODE_NOTBLANK;
import static com.openease.common.data.lang.MessageKeys.VALIDATION_PASSWORD_LENGTH;
import static com.openease.common.data.lang.MessageKeys.VALIDATION_PASSWORD_NOTBLANK;
import static com.openease.common.data.model.account.Account.PASSWORD_LENGTH_MIN;

/**
 * Account: Reset password request
 *
 * @author Alan Czajkowski
 */
public class AccountResetPasswordRequest extends BaseManagerModel {

  @NotBlank(message = "{" + VALIDATION_PASSWORD_NOTBLANK + "}")
  @Size(min = PASSWORD_LENGTH_MIN, message = "{" + VALIDATION_PASSWORD_LENGTH + "}")
  private String password;

  @NotBlank(message = "{" + VALIDATION_ACCOUNT_PASSWORDRESETCODE_NOTBLANK + "}")
  private String passwordResetCode;

  public String getPassword() {
    return password;
  }

  public AccountResetPasswordRequest setPassword(String password) {
    this.password = password;
    return this;
  }

  public String getPasswordResetCode() {
    return passwordResetCode;
  }

  public AccountResetPasswordRequest setPasswordResetCode(String passwordResetCode) {
    this.passwordResetCode = passwordResetCode;
    return this;
  }

  private abstract class MixIn extends AccountResetPasswordRequest {

    @Override
    @JsonSerialize(using = JsonPasswordSerializer.class)
    public String getPassword() {
      return null;
    }

    @Override
    @JsonSerialize(using = JsonPasswordSerializer.class)
    public String getPasswordResetCode() {
      return null;
    }

  }

  @Override
  protected Class<?> getMixInClass() {
    return AccountResetPasswordRequest.MixIn.class;
  }

}
