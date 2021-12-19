package com.openease.common.data.model.account;

import java.util.Date;
import java.util.Locale;
import java.util.Set;

/**
 * Interface: Account
 *
 * @author Alan Czajkowski
 */
public interface AccountInterface<ACCOUNT extends AccountInterface> {

  String getId();

  String getUsername();

  ACCOUNT setUsername(String username);

  PhoneNumber getPhoneNumber();

  ACCOUNT setPhoneNumber(PhoneNumber phoneNumber);

  Tier getTier();

  ACCOUNT setTier(Tier tier);

  boolean isEnabled();

  ACCOUNT setEnabled(boolean enabled);

  boolean isVerified();

  ACCOUNT setVerified(boolean verified);

  boolean isAccountNonExpired();

  ACCOUNT setAccountNonExpired(boolean accountNonExpired);

  boolean isAccountNonLocked();

  ACCOUNT setAccountNonLocked(boolean accountNonLocked);

  boolean isCredentialsNonExpired();

  ACCOUNT setCredentialsNonExpired(boolean credentialsNonExpired);

  Set<String> getRoles();

  ACCOUNT setRoles(Set<String> roles);

  Locale getLocale();

  ACCOUNT setLocale(Locale locale);

  String getFirstName();

  ACCOUNT setFirstName(String firstName);

  String getLastName();

  ACCOUNT setLastName(String lastName);

  Gender getGender();

  ACCOUNT setGender(Gender gender);

  String getCompanyName();

  ACCOUNT setCompanyName(String companyName);

  String getImageUrl();

  ACCOUNT setImageUrl(String imageUrl);

  OAuth2 getOAuth2();

  ACCOUNT setOAuth2(OAuth2 oAuth2);

  Date getCreated();

  ACCOUNT setCreated(Date created);

  Date getLastModified();

  ACCOUNT setLastModified(Date lastModified);

}
