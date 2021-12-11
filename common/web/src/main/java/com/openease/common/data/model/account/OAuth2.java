package com.openease.common.data.model.account;

import java.util.HashMap;
import java.util.Map;

/**
 * OAuth 2.0 model
 *
 * @author Alan Czajkowski
 */
public class OAuth2 {

  private OAuth2Provider provider;

  private Map<String, Object> userAttributes;

  public OAuth2() {
    this.provider = null;
    this.userAttributes = new HashMap<>();
  }

  public OAuth2Provider getProvider() {
    return provider;
  }

  public OAuth2 setProvider(OAuth2Provider provider) {
    this.provider = provider;
    return this;
  }

  public Map<String, Object> getUserAttributes() {
    return userAttributes;
  }

  public OAuth2 setUserAttributes(Map<String, Object> userAttributes) {
    this.userAttributes = userAttributes;
    return this;
  }

}
