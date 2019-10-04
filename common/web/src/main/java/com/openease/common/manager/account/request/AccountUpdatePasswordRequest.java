package com.openease.common.manager.account.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.openease.common.manager.base.model.BaseManagerModel;
import com.openease.common.util.JsonPasswordSerializer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.openease.common.data.lang.MessageKeys.VALIDATION_PASSWORD_LENGTH;
import static com.openease.common.data.lang.MessageKeys.VALIDATION_PASSWORD_NOTBLANK;
import static com.openease.common.data.model.account.Account.PASSWORD_LENGTH_MIN;

/**
 * Account: Update password request
 *
 * @author Alan Czajkowski
 */
public class AccountUpdatePasswordRequest extends BaseManagerModel {

  @NotBlank(message = "{" + VALIDATION_PASSWORD_NOTBLANK + "}")
  private String currentPassword;

  @NotBlank(message = "{" + VALIDATION_PASSWORD_NOTBLANK + "}")
  @Size(min = PASSWORD_LENGTH_MIN, message = "{" + VALIDATION_PASSWORD_LENGTH + "}")
  private String newPassword;

  public String getCurrentPassword() {
    return currentPassword;
  }

  public AccountUpdatePasswordRequest setCurrentPassword(String currentPassword) {
    this.currentPassword = currentPassword;
    return this;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public AccountUpdatePasswordRequest setNewPassword(String newPassword) {
    this.newPassword = newPassword;
    return this;
  }

  private abstract class MixIn extends AccountUpdatePasswordRequest {

    @Override
    @JsonSerialize(using = JsonPasswordSerializer.class)
    public String getCurrentPassword() {
      return null;
    }

    @Override
    @JsonSerialize(using = JsonPasswordSerializer.class)
    public String getNewPassword() {
      return null;
    }

  }

  @Override
  protected Class<?> getMixInClass() {
    return AccountUpdatePasswordRequest.MixIn.class;
  }

}
