package com.openease.common.manager.account.request;

import com.openease.common.manager.base.model.BaseManagerModel;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import static com.openease.common.data.lang.MessageKeys.VALIDATION_EMAIL_INVALID;
import static com.openease.common.data.lang.MessageKeys.VALIDATION_EMAIL_NOTBLANK;

/**
 * Account: Check email available request
 *
 * @author Alan Czajkowski
 */
public class AccountCheckEmailAvailableRequest extends BaseManagerModel {

  @NotBlank(message = "{" + VALIDATION_EMAIL_NOTBLANK + "}")
  @Email(message = "{" + VALIDATION_EMAIL_INVALID + "}")
  private String email;

  public String getEmail() {
    return email;
  }

  public AccountCheckEmailAvailableRequest setEmail(String email) {
    this.email = email;
    return this;
  }

}
