package com.openease.common.manager.account.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.openease.common.data.model.account.Gender;
import com.openease.common.manager.base.model.BaseManagerModel;
import com.openease.common.util.JsonPasswordSerializer;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.openease.common.data.lang.MessageKeys.VALIDATION_ACCOUNT_FIRSTNAME_NOTBLANK;
import static com.openease.common.data.lang.MessageKeys.VALIDATION_ACCOUNT_LASTNAME_NOTBLANK;
import static com.openease.common.data.lang.MessageKeys.VALIDATION_EMAIL_INVALID;
import static com.openease.common.data.lang.MessageKeys.VALIDATION_EMAIL_NOTBLANK;
import static com.openease.common.data.lang.MessageKeys.VALIDATION_PASSWORD_LENGTH;
import static com.openease.common.data.lang.MessageKeys.VALIDATION_PASSWORD_NOTBLANK;
import static com.openease.common.data.model.account.Account.PASSWORD_LENGTH_MIN;
import static com.openease.common.manager.lang.MessageKeys.VALIDATION_CAPTCHAVERIFYREQUEST_USERTOKEN_NOTBLANK;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trim;

/**
 * Account: Create request
 *
 * @author Alan Czajkowski
 */
public class AccountCreateRequest extends BaseManagerModel {

  @NotBlank(message = "{" + VALIDATION_EMAIL_NOTBLANK + "}")
  @Email(message = "{" + VALIDATION_EMAIL_INVALID + "}")
  private String email;

  @NotBlank(message = "{" + VALIDATION_PASSWORD_NOTBLANK + "}")
  @Size(min = PASSWORD_LENGTH_MIN, message = "{" + VALIDATION_PASSWORD_LENGTH + "}")
  private String password;

  @NotBlank(message = "{" + VALIDATION_ACCOUNT_FIRSTNAME_NOTBLANK + "}")
  private String firstName;

  @NotBlank(message = "{" + VALIDATION_ACCOUNT_LASTNAME_NOTBLANK + "}")
  private String lastName;

  private Gender gender;

  private String companyName;

  @NotBlank(message = "{" + VALIDATION_CAPTCHAVERIFYREQUEST_USERTOKEN_NOTBLANK + "}")
  private String captchaUserToken;

  public String getEmail() {
    return email;
  }

  public AccountCreateRequest setEmail(String email) {
    this.email = trim(lowerCase(email));
    return this;
  }

  public String getPassword() {
    return password;
  }

  public AccountCreateRequest setPassword(String password) {
    this.password = password;
    return this;
  }

  public String getFirstName() {
    return firstName;
  }

  public AccountCreateRequest setFirstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  public String getLastName() {
    return lastName;
  }

  public AccountCreateRequest setLastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  public Gender getGender() {
    return gender;
  }

  public AccountCreateRequest setGender(Gender gender) {
    this.gender = gender;
    return this;
  }

  public String getCompanyName() {
    return companyName;
  }

  public AccountCreateRequest setCompanyName(String companyName) {
    this.companyName = companyName;
    return this;
  }

  public String getCaptchaUserToken() {
    return captchaUserToken;
  }

  public AccountCreateRequest setCaptchaUserToken(String captchaUserToken) {
    this.captchaUserToken = captchaUserToken;
    return this;
  }

  private abstract class MixIn extends AccountCreateRequest {

    @Override
    @JsonSerialize(using = JsonPasswordSerializer.class)
    public String getPassword() {
      return null;
    }

  }

  @Override
  protected Class<?> getMixInClass() {
    return AccountCreateRequest.MixIn.class;
  }

}
