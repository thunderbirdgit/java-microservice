package com.openease.common.manager.email.request;

import com.openease.common.manager.base.model.BaseManagerModel;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import static com.openease.common.manager.lang.MessageKeys.VALIDATION_EMAILRECIPIENT_EMAIL_INVALID;
import static com.openease.common.manager.lang.MessageKeys.VALIDATION_EMAILRECIPIENT_EMAIL_NOTBLANK;

/**
 * Email contact
 *
 * @author Alan Czajkowski
 */
public class EmailContact extends BaseManagerModel {

  @NotBlank(message = "{" + VALIDATION_EMAILRECIPIENT_EMAIL_NOTBLANK + "}")
  @Email(message = "{" + VALIDATION_EMAILRECIPIENT_EMAIL_INVALID + "}")
  private String email;

  private String name;

  public String getEmail() {
    return email;
  }

  public EmailContact setEmail(String email) {
    this.email = email;
    return this;
  }

  public String getName() {
    return name;
  }

  public EmailContact setName(String name) {
    this.name = name;
    return this;
  }

}
