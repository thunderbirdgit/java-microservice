package com.openease.common.data.model.account;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.openease.common.data.model.base.BaseAuditDataModel;
import com.openease.common.util.JsonPasswordSerializer;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.openease.common.data.lang.MessageKeys.VALIDATION_ACCOUNT_FIRSTNAME_NOTBLANK;
import static com.openease.common.data.lang.MessageKeys.VALIDATION_ACCOUNT_LASTNAME_NOTBLANK;
import static com.openease.common.data.lang.MessageKeys.VALIDATION_ACCOUNT_ROLES_NOTEMPTY;
import static com.openease.common.data.lang.MessageKeys.VALIDATION_ACCOUNT_TIER_NOTNULL;
import static com.openease.common.data.lang.MessageKeys.VALIDATION_EMAIL_INVALID;
import static com.openease.common.data.lang.MessageKeys.VALIDATION_EMAIL_NOTBLANK;
import static com.openease.common.data.lang.MessageKeys.VALIDATION_PASSWORD_LENGTH;
import static com.openease.common.data.lang.MessageKeys.VALIDATION_PASSWORD_NOTBLANK;
import static com.openease.common.data.model.account.Account.ACCOUNTS;
import static com.openease.common.data.model.account.Gender.UNKNOWN;
import static com.openease.common.data.model.account.Tier.TIER0;
import static com.openease.common.util.JavaUtils.notNull;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trim;

/**
 * Account data model
 *
 * @author Alan Czajkowski
 */
@Document(indexName = ACCOUNTS)
public class Account extends BaseAuditDataModel<Account> implements AccountInterface<Account>, UserDetails, OAuth2User {

  public static final String ACCOUNTS = "accounts";

  public static final int PASSWORD_LENGTH_MIN = 10;
  public static final int PASSWORD_RESET_CODE_LENGTH = 30;
  public static final int VERIFICATION_CODE_LENGTH = 30;
  public static final Tier DEFAULT_TIER = TIER0;

  @NotBlank(message = "{" + VALIDATION_EMAIL_NOTBLANK + "}")
  @Email(message = "{" + VALIDATION_EMAIL_INVALID + "}")
  private String username;

  @NotBlank(message = "{" + VALIDATION_PASSWORD_NOTBLANK + "}")
  @Size(min = PASSWORD_LENGTH_MIN, message = "{" + VALIDATION_PASSWORD_LENGTH + "}")
  private String password;

  private String passwordResetCode;
  private Date passwordResetCreated;

  private String verificationCode;

  @Field(type = FieldType.Object)
  private PhoneNumber phoneNumber;

  @NotNull(message = "{" + VALIDATION_ACCOUNT_TIER_NOTNULL + "}")
  private Tier tier;

  private boolean enabled;

  private boolean verified;

  private boolean accountNonExpired;

  private boolean accountNonLocked;

  private boolean credentialsNonExpired;

  @NotEmpty(message = "{" + VALIDATION_ACCOUNT_ROLES_NOTEMPTY + "}")
  private Set<String> roles;

  @JsonProperty
  private Locale locale;

  @NotBlank(message = "{" + VALIDATION_ACCOUNT_FIRSTNAME_NOTBLANK + "}")
  private String firstName;

  @NotBlank(message = "{" + VALIDATION_ACCOUNT_LASTNAME_NOTBLANK + "}")
  private String lastName;

  private Gender gender;

  private String companyName;

  private String imageUrl;

  @Field(type = FieldType.Object)
  private OAuth2 oAuth2;

  public Account() {
    this(null);
  }

  /**
   * {@link Account#enabled}
   * - set to true via sign-up workflow
   * {@link Account#verified}
   * - set to true via email verification
   *
   * @param id account identifier
   */
  public Account(String id) {
    super(id);
    this.username = null;
    this.password = null;
    this.passwordResetCode = null;
    this.passwordResetCreated = null;
    this.verificationCode = randomAlphanumeric(VERIFICATION_CODE_LENGTH);
    this.phoneNumber = new PhoneNumber();
    this.tier = DEFAULT_TIER;
    this.enabled = false;
    this.verified = false;
    this.accountNonExpired = true;
    this.accountNonLocked = true;
    this.credentialsNonExpired = true;
    this.roles = new HashSet<>();
    this.locale = DEFAULT_LOCALE;
    this.firstName = null;
    this.lastName = null;
    this.gender = UNKNOWN;
    this.companyName = null;
    this.imageUrl = null;
    this.oAuth2 = new OAuth2();
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public Account setUsername(String username) {
    this.username = trim(lowerCase(username));
    return this;
  }

  @Override
  public String getPassword() {
    return password;
  }

  public Account setPassword(String password) {
    this.password = password;
    return this;
  }

  public String getPasswordResetCode() {
    return passwordResetCode;
  }

  public Account setPasswordResetCode(String passwordResetCode) {
    this.passwordResetCode = passwordResetCode;
    this.passwordResetCreated = passwordResetCode == null
        ? null
        : new Date();
    return this;
  }

  public Date getPasswordResetCreated() {
    return passwordResetCreated;
  }

  public Account setPasswordResetCreated(Date passwordResetCreated) {
    this.passwordResetCreated = passwordResetCreated;
    return this;
  }

  public String getVerificationCode() {
    return verificationCode;
  }

  public Account setVerificationCode(String verificationCode) {
    this.verificationCode = verificationCode;
    return this;
  }

  @Override
  public PhoneNumber getPhoneNumber() {
    return phoneNumber;
  }

  @Override
  public Account setPhoneNumber(PhoneNumber phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  @Override
  public Tier getTier() {
    return tier;
  }

  @Override
  public Account setTier(Tier tier) {
    this.tier = tier;
    return this;
  }

  public Account setTier(String tierName) {
    Tier tier = TIER0;
    if (isNotBlank(tierName)) {
      try {
        tier = Tier.valueOf(tierName);
      } catch (IllegalArgumentException iae) {
        // ignore
      }
    }
    setTier(tier);
    return this;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public Account setEnabled(boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  @Override
  public boolean isVerified() {
    return verified;
  }

  @Override
  public Account setVerified(boolean verified) {
    this.verified = verified;
    return this;
  }

  @Override
  public boolean isAccountNonExpired() {
    return accountNonExpired;
  }

  @Override
  public Account setAccountNonExpired(boolean accountNonExpired) {
    this.accountNonExpired = accountNonExpired;
    return this;
  }

  @Override
  public boolean isAccountNonLocked() {
    return accountNonLocked;
  }

  @Override
  public Account setAccountNonLocked(boolean accountNonLocked) {
    this.accountNonLocked = accountNonLocked;
    return this;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return credentialsNonExpired;
  }

  @Override
  public Account setCredentialsNonExpired(boolean credentialsNonExpired) {
    this.credentialsNonExpired = credentialsNonExpired;
    return this;
  }

  @Override
  @JsonIgnore
  public Collection<GrantedAuthority> getAuthorities() {
    return notNull(roles)
        .stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toSet());
  }

  @Override
  public Set<String> getRoles() {
    return roles;
  }

  @Override
  public Account setRoles(Set<String> roles) {
    this.roles = roles;
    return this;
  }

  public Account setRole(Role role) {
    this.roles = new HashSet<>();
    if (role != null) {
      this.roles.add(role.toString());
    }
    return this;
  }

  public Account addRoles(Role... newRoles) {
    for (Role role : notNull(newRoles)) {
      if (role != null) {
        addRoles(role.toString());
      }
    }
    return this;
  }

  public Account addRoles(String... newRoles) {
    Set<String> roles = getRoles();
    roles.addAll(Arrays.asList(notNull(newRoles)));
    setRoles(roles);
    return this;
  }

  @Override
  @JsonIgnore
  public Locale getLocale() {
    return locale;
  }

  @JsonGetter("locale")
  public String getLocaleString() {
    return locale != null
        ? locale.toLanguageTag()
        : DEFAULT_LOCALE.toLanguageTag();
  }

  @Override
  @JsonIgnore
  public Account setLocale(Locale locale) {
    this.locale = Locale.forLanguageTag(locale.getLanguage());
    return this;
  }

  public Account setLocale(String locale) {
    this.locale = Locale.forLanguageTag(locale);
    return this;
  }

  @Override
  public String getFirstName() {
    return firstName;
  }

  @Override
  public Account setFirstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  @Override
  public String getLastName() {
    return lastName;
  }

  @Override
  public Account setLastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  @Override
  public Gender getGender() {
    return gender;
  }

  @Override
  public Account setGender(Gender gender) {
    this.gender = gender;
    return this;
  }

  public Account setGender(String genderName) {
    Gender gender = UNKNOWN;
    if (isNotBlank(genderName)) {
      try {
        gender = Gender.valueOf(genderName);
      } catch (IllegalArgumentException iae) {
        // ignore
      }
    }
    this.gender = gender;
    return this;
  }

  @Override
  public String getCompanyName() {
    return companyName;
  }

  @Override
  public Account setCompanyName(String companyName) {
    this.companyName = companyName;
    return this;
  }

  @Override
  public String getImageUrl() {
    return imageUrl;
  }

  @Override
  public Account setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
    return this;
  }

  @Override
  public OAuth2 getOAuth2() {
    return oAuth2;
  }

  @Override
  public Account setOAuth2(OAuth2 oAuth2) {
    this.oAuth2 = oAuth2;
    return this;
  }

  @Override
  @JsonIgnore
  public Map<String, Object> getAttributes() {
    return getOAuth2().getUserAttributes();
  }

  @Override
  @JsonIgnore
  public String getName() {
    return getUsername();
  }

  private abstract class MixIn extends Account {

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
    return Account.MixIn.class;
  }

}
