package com.openease.common.manager.account.response;

import com.openease.common.data.model.account.AccountScrubbed;
import com.openease.common.manager.base.model.BaseManagerModel;

/**
 * Account: Update response
 *
 * @author Alan Czajkowski
 */
public class AccountUpdateResponse extends BaseManagerModel {

  private String jwt;

  private AccountScrubbed account;

  public AccountUpdateResponse() {
    this.jwt = null;
    this.account = null;
  }

  public String getJwt() {
    return jwt;
  }

  public AccountUpdateResponse setJwt(String jwt) {
    this.jwt = jwt;
    return this;
  }

  public AccountScrubbed getAccount() {
    return account;
  }

  public AccountUpdateResponse setAccount(AccountScrubbed account) {
    this.account = account;
    return this;
  }

}
