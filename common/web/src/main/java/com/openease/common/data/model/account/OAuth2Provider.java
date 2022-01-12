package com.openease.common.data.model.account;

import io.jsonwebtoken.SignatureAlgorithm;

/**
 * OAuth 2.0 provider enum
 *
 * @author Alan Czajkowski
 */
public enum OAuth2Provider {

  GOOGLE,
  APPLE,
  FACEBOOK;

  public static class AdditionalDetails {
    private String clientId;
    private String teamId;
    private String keyId;
    private String privateKey;
    private String audience;
    private SignatureAlgorithm jwtSignatureAlgorithm;

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
  }

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
