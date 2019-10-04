package com.openease.common.data.dao.account;

import com.openease.common.data.dao.base.BaseDao;
import com.openease.common.data.model.account.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

/**
 * Account DAO
 *
 * @author Alan Czajkowski
 */
public interface AccountDao extends BaseDao<Account> {

  Account findByUsername(String username);

  Page<Account> findByFirstName(String firstName, Pageable pageable);

  Page<Account> findByLastName(String lastName, Pageable pageable);

  Page<Account> findByCompanyName(String companyName, Pageable pageable);

  Account findByVerificationCode(String verificationCode);

  Account findByPasswordResetCode(String passwordResetCode);

  Set<String> findIdByEnabledAndAccountNonExpired(boolean enabled, boolean accountNonExpired);

}
