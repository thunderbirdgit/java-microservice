package com.openease.common.manager.security.response;

import com.openease.common.data.model.account.HasAccountId;
import com.openease.common.manager.base.model.BaseManagerModel;

/**
 * Security: Create authentication (sign-in) response
 *
 * @author Alan Czajkowski
 */
public class SecurityCreateAuthResponse extends BaseManagerModel implements HasAccountId<SecurityCreateAuthResponse> {

  private String token;

  private String accountId;

  public SecurityCreateAuthResponse() {
    this.token = null;
    this.accountId = null;
  }

  public String getToken() {
    return token;
  }

  public SecurityCreateAuthResponse setToken(String token) {
    this.token = token;
    return this;
  }

  @Override
  public String getAccountId() {
    return accountId;
  }

  @Override
  public SecurityCreateAuthResponse setAccountId(String accountId) {
    this.accountId = accountId;
    return this;
  }

}
