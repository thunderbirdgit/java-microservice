package com.openease.common.manager.security.response;

import com.openease.common.data.model.account.HasAccountId;
import com.openease.common.manager.base.model.BaseManagerModel;

/**
 * Security: Create authentication (sign-in) response
 *
 * @author Alan Czajkowski
 */
public class SecurityCreateAuthResponse extends BaseManagerModel implements HasAccountId<SecurityCreateAuthResponse> {

  private String jwt;

  private String accountId;

  public SecurityCreateAuthResponse() {
    this.jwt = null;
    this.accountId = null;
  }

  public String getJwt() {
    return jwt;
  }

  public SecurityCreateAuthResponse setJwt(String jwt) {
    this.jwt = jwt;
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
