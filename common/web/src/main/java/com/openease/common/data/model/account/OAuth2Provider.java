package com.openease.common.data.model.account;

import io.jsonwebtoken.SignatureAlgorithm;

/**
 * OAuth 2.0 provider enum
 *
 * @author Alan Czajkowski
 */
public enum OAuth2Provider {

  GOOGLE,
  APPLE;
//  FACEBOOK;

  public static final String OAUTH2_APPLE_CLIENT_SECRET = "OAUTH2_APPLE_CLIENT_SECRET";

  public static class AdditionalDetails {
    private String clientId;
    private String teamId;
    private String keyId;
    private String privateKey;
    private String audience;
    private SignatureAlgorithm jwtSignatureAlgorithm;
    private int jwtExpirationDays;

    public String getClientId() {
      return clientId;
    }

    public AdditionalDetails setClientId(String clientId) {
      this.clientId = clientId;
      return this;
    }

    public String getTeamId() {
      return teamId;
    }

    public AdditionalDetails setTeamId(String teamId) {
      this.teamId = teamId;
      return this;
    }

    public String getKeyId() {
      return keyId;
    }

    public AdditionalDetails setKeyId(String keyId) {
      this.keyId = keyId;
      return this;
    }

    public String getPrivateKey() {
      return privateKey;
    }

    public AdditionalDetails setPrivateKey(String privateKey) {
      this.privateKey = privateKey;
      return this;
    }

    public String getAudience() {
      return audience;
    }

    public AdditionalDetails setAudience(String audience) {
      this.audience = audience;
      return this;
    }

    public SignatureAlgorithm getJwtSignatureAlgorithm() {
      return jwtSignatureAlgorithm;
    }

    public AdditionalDetails setJwtSignatureAlgorithm(SignatureAlgorithm jwtSignatureAlgorithm) {
      this.jwtSignatureAlgorithm = jwtSignatureAlgorithm;
      return this;
    }

    public int getJwtExpirationDays() {
      return jwtExpirationDays;
    }

    public AdditionalDetails setJwtExpirationDays(int jwtExpirationDays) {
      this.jwtExpirationDays = jwtExpirationDays;
      return this;
    }
  }

  /**
   * Apple OAuth 2.0 sign-in returns these user details as a parameter in the
   * HTTP POST callback, but the user's name is only returned on the very
   * first time the user signs in.
   * Apple documentation:
   * <pre>
   *   When someone uses your app and Sign in with Apple for the very first time,
   *   the identification servers return the user status. Subsequent attempts do
   *   not return the user status.
   * </pre>
   *
   * @see <a href="https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_rest_api/authenticating_users_with_sign_in_with_apple">Apple Developer | OAuth 2.0 | Sign-in</a>
   */
  public static class AppleUserDetails {
    private Name name;
    private String email;

    public Name getName() {
      return name;
    }

    public AppleUserDetails setName(Name name) {
      this.name = name;
      return this;
    }

    public String getEmail() {
      return email;
    }

    public AppleUserDetails setEmail(String email) {
      this.email = email;
      return this;
    }

    public static class Name {
      private String firstName;
      private String lastName;

      public String getFirstName() {
        return firstName;
      }

      public Name setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
      }

      public String getLastName() {
        return lastName;
      }

      public Name setLastName(String lastName) {
        this.lastName = lastName;
        return this;
      }
    }
  }

}
