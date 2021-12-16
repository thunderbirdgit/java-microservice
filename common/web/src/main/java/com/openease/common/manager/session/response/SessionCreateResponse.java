package com.openease.common.manager.session.response;

import com.openease.common.data.model.account.HasAccountId;
import com.openease.common.manager.base.model.BaseManagerModel;

/**
 * Session create (sign-in) response
 *
 * @author Alan Czajkowski
 */
public class SessionCreateResponse extends BaseManagerModel implements HasAccountId<SessionCreateResponse> {

  private String accountId;

  private String jwt;

  public SessionCreateResponse() {
    this.accountId = null;
  }

  @Override
  public String getAccountId() {
    return accountId;
  }

  @Override
  public SessionCreateResponse setAccountId(String accountId) {
    this.accountId = accountId;
    return this;
  }

  public String getJwt() {
    return jwt;
  }

  public SessionCreateResponse setJwt(String jwt) {
    this.jwt = jwt;
    return this;
  }

}
