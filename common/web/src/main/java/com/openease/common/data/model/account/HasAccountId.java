package com.openease.common.data.model.account;

/**
 * Interface: HasAccountId
 *
 * @author Alan Czajkowski
 */
public interface HasAccountId<T> {

  String getAccountId();

  T setAccountId(String accountId);

}
