package com.openease.common.data.model.account;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.openease.common.data.model.base.BaseAuditDataModel;
import com.openease.common.util.JsonPasswordSerializer;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import static com.openease.common.data.lang.MessageKeys.VALIDATION_ACCOUNT_FIRSTNAME_NOTBLANK;
import static com.openease.common.data.lang.MessageKeys.VALIDATION_ACCOUNT_LASTNAME_NOTBLANK;
import static com.openease.common.data.lang.MessageKeys.VALIDATION_EMAIL_INVALID;
import static com.openease.common.data.lang.MessageKeys.VALIDATION_EMAIL_NOTBLANK;

/**
 * {@link Account} scrubbed for front-end use
 *
 * @author Alan Czajkowski
 */
public class AccountScrubbed extends BaseAuditDataModel<AccountScrubbed> implements AccountInterface<AccountScrubbed> {

  private Account account;

  private String passwordVerification;

  public AccountScrubbed() {
    super(null);
    this.account = new Account(null);
  }

  public AccountScrubbed(Account account) {
    super(account.getId());
    this.account = account;
  }

  public String getPasswordVerification() {
    return passwordVerification;
  }

  public AccountScrubbed setPasswordVerification(String passwordVerification) {
    this.passwordVerification = passwordVerification;
    return this;
  }

  @Override
  public String getId() {
    return account.getId();
  }

  @Override
  public AccountScrubbed setId(String id) {
    super.setId(id);
    account.setId(id);
    return this;
  }

  @NotBlank(message = "{" + VALIDATION_EMAIL_NOTBLANK + "}")
  @Email(message = "{" + VALIDATION_EMAIL_INVALID + "}")
  @Override
  public String getUsername() {
    return account.getUsername();
  }

  @Override
  public AccountScrubbed setUsername(String username) {
    account.setUsername(username);
    return this;
  }

  @Override
  public PhoneNumber getPhoneNumber() {
    return account.getPhoneNumber();
  }

  @Override
  public AccountScrubbed setPhoneNumber(PhoneNumber phoneNumber) {
    account.setPhoneNumber(phoneNumber);
    return this;
  }

  @Override
  public Tier getTier() {
    return account.getTier();
  }

  @Override
  public AccountScrubbed setTier(Tier tier) {
    account.setTier(tier);
    return this;
  }

  @Override
  public boolean isEnabled() {
    return account.isEnabled();
  }

  @Override
  public AccountScrubbed setEnabled(boolean enabled) {
    account.setEnabled(enabled);
    return this;
  }

  @Override
  public boolean isVerified() {
    return account.isVerified();
  }

  @Override
  public AccountScrubbed setVerified(boolean verified) {
    account.setVerified(verified);
    return this;
  }

  @Override
  public boolean isAccountNonExpired() {
    return account.isAccountNonExpired();
  }

  @Override
  public AccountScrubbed setAccountNonExpired(boolean accountNonExpired) {
    account.setAccountNonExpired(accountNonExpired);
    return this;
  }

  @Override
  public boolean isAccountNonLocked() {
    return account.isAccountNonLocked();
  }

  @Override
  public AccountScrubbed setAccountNonLocked(boolean accountNonLocked) {
    account.setAccountNonLocked(accountNonLocked);
    return this;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return account.isCredentialsNonExpired();
  }

  @Override
  public AccountScrubbed setCredentialsNonExpired(boolean credentialsNonExpired) {
    account.setCredentialsNonExpired(credentialsNonExpired);
    return this;
  }

  @Override
  public Set<String> getRoles() {
    return account.getRoles();
  }

  @Override
  public AccountScrubbed setRoles(Set<String> roles) {
    account.setRoles(roles);
    return this;
  }

  @Override
  @JsonIgnore
  public Locale getLocale() {
    return account.getLocale();
  }

  @JsonGetter("locale")
  public String getLocaleString() {
    return account.getLocaleString();
  }

  @Override
  @JsonIgnore
  public AccountScrubbed setLocale(Locale locale) {
    account.setLocale(locale);
    return this;
  }

  public AccountScrubbed setLocale(String locale) {
    account.setLocale(locale);
    return this;
  }

  @NotBlank(message = "{" + VALIDATION_ACCOUNT_FIRSTNAME_NOTBLANK + "}")
  @Override
  public String getFirstName() {
    return account.getFirstName();
  }

  @Override
  public AccountScrubbed setFirstName(String firstName) {
    account.setFirstName(firstName);
    return this;
  }

  @NotBlank(message = "{" + VALIDATION_ACCOUNT_LASTNAME_NOTBLANK + "}")
  @Override
  public String getLastName() {
    return account.getLastName();
  }

  @Override
  public AccountScrubbed setLastName(String lastName) {
    account.setLastName(lastName);
    return this;
  }

  @Override
  public Gender getGender() {
    return account.getGender();
  }

  @Override
  public AccountScrubbed setGender(Gender gender) {
    account.setGender(gender);
    return this;
  }

  @Override
  public String getCompanyName() {
    return account.getCompanyName();
  }

  @Override
  public AccountScrubbed setCompanyName(String companyName) {
    account.setCompanyName(companyName);
    return this;
  }

  @Override
  public Date getCreated() {
    return account.getCreated();
  }

  @Override
  public AccountScrubbed setCreated(Date created) {
    account.setCreated(created);
    return this;
  }

  @Override
  public Date getLastModified() {
    return account.getLastModified();
  }

  @Override
  public AccountScrubbed setLastModified(Date lastModified) {
    account.setLastModified(lastModified);
    return this;
  }

  private abstract class MixIn extends AccountScrubbed {

    protected MixIn() {
      super(account);
    }

    @Override
    @JsonSerialize(using = JsonPasswordSerializer.class)
    public String getPasswordVerification() {
      return null;
    }

  }

  @Override
  protected Class<?> getMixInClass() {
    return AccountScrubbed.MixIn.class;
  }

}
