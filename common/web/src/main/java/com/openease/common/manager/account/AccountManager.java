package com.openease.common.manager.account;

import com.openease.common.config.Config;
import com.openease.common.data.dao.account.AccountDao;
import com.openease.common.data.exception.GeneralDataException;
import com.openease.common.data.model.account.Account;
import com.openease.common.data.model.account.AccountScrubbed;
import com.openease.common.manager.account.request.AccountCheckEmailAvailableRequest;
import com.openease.common.manager.account.request.AccountCreateRequest;
import com.openease.common.manager.account.request.AccountResetPasswordRequest;
import com.openease.common.manager.account.request.AccountSendPasswordResetCodeRequest;
import com.openease.common.manager.account.request.AccountUpdatePasswordRequest;
import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.page.PageManager;
import com.openease.common.manager.task.email.SendNotifyPasswordChangeEmailTask;
import com.openease.common.manager.task.email.SendNotifyUsernameChangeEmailTask;
import com.openease.common.manager.task.email.SendResetPasswordEmailTask;
import com.openease.common.manager.task.email.SendVerifyAccountEmailTask;
import com.openease.common.manager.task.email.SendWelcomeEmailTask;
import com.openease.common.util.spring.BeanLocator;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;

import static com.openease.common.data.lang.MessageKeys.CRUD_BADREQUEST;
import static com.openease.common.data.lang.MessageKeys.CRUD_NOTFOUND;
import static com.openease.common.data.lang.MessageKeys.CRUD_UPDATE_STALE;
import static com.openease.common.data.model.account.Account.PASSWORD_RESET_CODE_LENGTH;
import static com.openease.common.data.model.account.Role.ADMIN;
import static com.openease.common.data.model.account.Role.USER;
import static com.openease.common.manager.lang.MessageKeys.MANAGER_ACCOUNT_CREDENTIALS_INVALID;
import static com.openease.common.manager.lang.MessageKeys.MANAGER_ACCOUNT_DISABLED;
import static com.openease.common.manager.lang.MessageKeys.MANAGER_ACCOUNT_LOCKED;
import static com.openease.common.manager.lang.MessageKeys.MANAGER_ACCOUNT_PASSWORDS_IDENTICAL;
import static com.openease.common.manager.lang.MessageKeys.MANAGER_ACCOUNT_USERNAME_UNAVAILABLE;
import static org.apache.commons.lang3.ObjectUtils.notEqual;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.substring;
import static org.apache.commons.lang3.StringUtils.trim;

/**
 * Account manager
 *
 * @author Alan Czajkowski
 */
@Service
@DependsOn({"elasticsearchEmbeddedServer"})
public class AccountManager implements UserDetailsService, UserDetailsPasswordService {

  private static final transient Logger LOG = LogManager.getLogger(AccountManager.class);

  @Autowired
  private Config config;

  @Autowired
  private AccountDao accountDao;

  @Autowired
  private PageManager pageManager;

  @Autowired
  @Qualifier("defaultAsyncTaskExecutor")
  private AsyncTaskExecutor asyncTaskExecutor;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");

    LOG.debug("provision test accounts with role: {}", () -> ADMIN);
    for (Account account : config.getTestAccounts().get(ADMIN)) {
      encryptPassword(account, account.getPassword());
      Optional<Account> optional = accountDao.findById(account.getId());
      if (optional.isPresent()) {
        try {
          account.setCreated(optional.get().getCreated());
          account.setLastModified(optional.get().getLastModified());
          accountDao.update(account);
        } catch (GeneralDataException de) {
          LOG.warn(de.getMessage());
        }
      } else {
        accountDao.save(account);
      }
    }

    LOG.debug("provision test accounts with role: {}", () -> USER);
    for (Account account : config.getTestAccounts().get(USER)) {
      encryptPassword(account, account.getPassword());
      Optional<Account> optional = accountDao.findById(account.getId());
      if (optional.isPresent()) {
        try {
          account.setCreated(optional.get().getCreated());
          account.setLastModified(optional.get().getLastModified());
          accountDao.update(account);
        } catch (GeneralDataException de) {
          LOG.warn(de.getMessage());
        }
      } else {
        accountDao.save(account);
      }
    }

    LOG.debug("Init finished");
  }

  public Account create(Account account, String password) throws GeneralManagerException {
    final Account finalAccount = account;
    LOG.debug("account: {}", () -> (finalAccount == null ? null : finalAccount.toStringUsingMixIn()));
    checkForNullAccount(account);

    LOG.debug("Validate account");
    validate(account);

    LOG.debug("Init account");
    initAccount(account, password);

    LOG.debug("Create account");
    account = accountDao.save(account);

    if (!account.isVerified()) {
      LOG.debug("Sending verification email");
      try {
        submitSendVerifyAccountEmailTask(account);
      } catch (Exception e) {
        LOG.error(e::getMessage, e);
        throw new RuntimeException("Something really bad happened", e);
      }
    } else {
      LOG.debug("Account is verified, sending welcome email");
      try {
        submitSendWelcomeEmailTask(account);
      } catch (Exception e) {
        LOG.error(e::getMessage, e);
        throw new RuntimeException("Something really bad happened", e);
      }
    }

    LOG.debug("account: {}", account::toStringUsingMixIn);
    return account;
  }

  public Account create(AccountCreateRequest request) throws GeneralManagerException {
    LOG.debug("request: {}", () -> (request == null ? null : request.toStringUsingMixIn()));
    if (request == null) {
      LOG.warn("request is null");
      throw new GeneralManagerException(CRUD_BADREQUEST, "request is null");
    }

    Account account = new Account()
        .setUsername(request.getEmail())
        .setFirstName(request.getFirstName())
        .setLastName(request.getLastName())
        .setGender(request.getGender())
        .setCompanyName(request.getCompanyName());

    account = create(account, request.getPassword());

    LOG.debug("account: {}", account::toStringUsingMixIn);
    return account;
  }

  public Account read(String id) throws GeneralManagerException {
    LOG.debug("id: {}", id);

    Account account;

    Optional<Account> optional = accountDao.findById(id);
    if (optional.isPresent()) {
      account = optional.get();
    } else {
      throw new GeneralManagerException(CRUD_NOTFOUND, "Account with id [" + id + "] *not* found");
    }

    return account;
  }

  public Account update(Account account) throws GeneralManagerException {
    final Account finalAccount = account;
    LOG.debug("account: {}", () -> (finalAccount == null ? null : finalAccount.toStringUsingMixIn()));
    checkForNullAccount(account);

    try {
      account = accountDao.update(account);
      LOG.trace("account: {}", account::toStringUsingMixIn);
    } catch (GeneralDataException de) {
      // re-throw
      throw new GeneralManagerException(de.getKey(), de.getMessage());
    }

    return account;
  }

  public Account update(AccountScrubbed accountScrubbed) throws GeneralManagerException {
    LOG.debug("accountScrubbed: {}", () -> (accountScrubbed == null ? null : accountScrubbed.toStringUsingMixIn()));
    checkForNullAccount(accountScrubbed);

    // check account exists
    Account accountToUpdate;
    Optional<Account> optional = accountDao.findById(accountScrubbed.getId());
    if (optional.isPresent()) {
      accountToUpdate = optional.get();
    } else {
      throw new GeneralManagerException(CRUD_NOTFOUND, "Account with id [" + accountScrubbed.getId() + "] *not* found");
    }
    LOG.trace("accountToUpdate: {}", accountToUpdate::toStringUsingMixIn);

    //TODO: remove since the DAO can do this now (test before removing)
    // check if updating is stale (via optimistic locking)
    if (notEqual(accountToUpdate.getLastModified(), accountScrubbed.getLastModified())) {
      throw new GeneralManagerException(CRUD_UPDATE_STALE, "Update rejected via optimistic locking");
    }

    // update account regular fields
    accountToUpdate.setFirstName(accountScrubbed.getFirstName())
        .setLastName(accountScrubbed.getLastName())
        .setGender(accountScrubbed.getGender())
        .setCompanyName(accountScrubbed.getCompanyName())
        .setLocale(accountScrubbed.getLocale());

    // update account username
    Account staleAccount = null;
    if (!equalsIgnoreCase(accountScrubbed.getUsername(), accountToUpdate.getUsername())) {
      LOG.debug("Account username (email) changed");
      // only verify password if account is non-OAuth2
      if (accountToUpdate.getOAuth2() == null) {
        LOG.debug("Verify current password");
        verifyPassword(accountToUpdate, accountScrubbed.getPasswordVerification());
      } else {
        LOG.debug("Account is attached to OAuth 2.0, skipping password verification");
      }

      // grab stale account for notification purposes
      staleAccount = copy(accountToUpdate);
      // update account username (email)
      accountToUpdate.setUsername(accountScrubbed.getUsername())
          // reset to unverified
          .setVerified(false)
          // remove any OAuth2 info since username (email) has changed
          .setOAuth2(null);
    }

    // update account phone number
    if (Objects.equals(accountScrubbed.getPhoneNumber(), accountToUpdate.getPhoneNumber())) {
      LOG.debug("Account phone number changed");
      LOG.debug("Verify current password");
      verifyPassword(accountToUpdate, accountScrubbed.getPasswordVerification());
    }

    Account updatedAccount = update(accountToUpdate);
    //TODO: send notification account info has changed

    // send emails if username (email) has changed (staleAccount will be non-null)
    if (staleAccount != null) {
      LOG.debug("Sending username changed notification to stale email address");
      try {
        submitSendNotifyUsernameChangeEmailTask(updatedAccount, staleAccount);
      } catch (Exception e) {
        LOG.error(e::getMessage, e);
        throw new RuntimeException("Something really bad happened", e);
      }

      LOG.debug("Sending verification email");
      try {
        submitSendVerifyAccountEmailTask(updatedAccount);
      } catch (Exception e) {
        LOG.error(e::getMessage, e);
        throw new RuntimeException("Something really bad happened", e);
      }
    }

    return updatedAccount;
  }

  public Account disable(Account disabledAccount) throws GeneralManagerException {
    LOG.debug("disabledAccount: {}", () -> (disabledAccount == null ? null : disabledAccount.toStringUsingMixIn()));
    checkForNullAccount(disabledAccount);

    // check account exists
    Account accountToDisable;
    Optional<Account> optional = accountDao.findById(disabledAccount.getId());
    if (optional.isPresent()) {
      accountToDisable = optional.get();
    } else {
      throw new GeneralManagerException(CRUD_NOTFOUND, "Account with id [" + disabledAccount.getId() + "] *not* found");
    }
    LOG.trace("accountToDisable: {}", accountToDisable::toStringUsingMixIn);

    accountToDisable.setEnabled(false);
    accountToDisable.setPasswordResetCode(null);

    try {
      accountToDisable = accountDao.update(accountToDisable);
    } catch (GeneralDataException de) {
      // re-throw
      throw new GeneralManagerException(de.getKey(), de.getMessage());
    }

    return accountToDisable;
  }

  public void sendVerificationCode(Account account) throws GeneralManagerException {
    final Account finalAccount = account;
    LOG.debug("account: {}", () -> (finalAccount == null ? null : finalAccount.toStringUsingMixIn()));
    checkForNullAccount(account);

    // re-fetch account
    Optional<Account> optional = accountDao.findById(account.getId());
    if (optional.isPresent()) {
      account = optional.get();
    } else {
      throw new GeneralManagerException(CRUD_NOTFOUND, "Account with id [" + account.getId() + "] *not* found");
    }

    if (!account.isVerified()) {
      LOG.debug("Sending verification email");
      try {
        submitSendVerifyAccountEmailTask(account);
      } catch (Exception e) {
        LOG.error(e::getMessage, e);
        throw new RuntimeException("Something really bad happened", e);
      }
    } else {
      LOG.debug("Account is already verified");
    }
  }

  public Set<String> findIdByEnabledAndAccountNonExpired(boolean enabled, boolean accountNonExpired) {
    return accountDao.findIdByEnabledAndAccountNonExpired(enabled, accountNonExpired);
  }

  public Account findByUsername(String username) {
    username = trim(lowerCase(username));
    LOG.debug("username: [{}]", username);

    Account account = null;
    if (username != null) {
      account = accountDao.findByUsername(username);
    }

    LOG.debug("Account found: {}", account != null);
    return account;
  }

  public void updatePassword(Account account, AccountUpdatePasswordRequest request, boolean skipPasswordVerification) throws GeneralManagerException {
    final Account finalAccount = account;
    LOG.debug("account: {}", () -> (finalAccount == null ? null : finalAccount.toStringUsingMixIn()));
    LOG.debug("skipPasswordVerification: {}", () -> skipPasswordVerification);
    checkForNullAccount(account);

    LOG.debug("request: {}", () -> (request == null ? null : request.toStringUsingMixIn()));
    if (request == null) {
      LOG.warn("request is null");
      throw new GeneralManagerException(CRUD_BADREQUEST, "request is null");
    }

    if (!skipPasswordVerification) {
      // only verify password if account is non-OAuth2
      if (account.getOAuth2() == null) {
        LOG.debug("Verify current password");
        verifyPassword(account, request.getCurrentPassword());
      } else {
        LOG.debug("Account is attached to OAuth 2.0, skipping password verification");
      }
    }

    LOG.debug("Verify new password is not identical to current password");
    if (StringUtils.equals(request.getCurrentPassword(), request.getNewPassword())) {
      throw new GeneralManagerException(MANAGER_ACCOUNT_PASSWORDS_IDENTICAL, "Passwords are identical");
    }

    LOG.debug("Set new password and update account");
    encryptPassword(account, request.getNewPassword());
    account.setOAuth2(null);
    try {
      account = accountDao.update(account);
    } catch (GeneralDataException de) {
      // re-throw
      throw new GeneralManagerException(de.getKey(), de.getMessage());
    }

    LOG.debug("Sending password changed notification email");
    try {
      submitSendNotifyPasswordChangeEmailTask(account);
    } catch (Exception e) {
      LOG.error(e::getMessage, e);
      throw new RuntimeException("Something really bad happened", e);
    }
  }

  @Override
  public UserDetails updatePassword(UserDetails user, String newPassword) {
    Account account = null;
    if (user != null) {
      account = findByUsername(user.getUsername());

      if (account != null) {
        AccountUpdatePasswordRequest request = new AccountUpdatePasswordRequest()
            .setCurrentPassword(null)
            .setNewPassword(newPassword);
        try {
          updatePassword(account, request, true);
        } catch (GeneralManagerException me) {
          LOG.error(me::getMessage, me);
        }
      }
    }

    return account;
  }

  public void updatePassword(Account account, AccountUpdatePasswordRequest request) throws GeneralManagerException {
    updatePassword(account, request, false);
  }

  public void verifyPassword(Account account, String password) throws GeneralManagerException {
    LOG.debug("account: {}", () -> (account == null ? null : account.toStringUsingMixIn()));
    if (account == null) {
      throw new GeneralManagerException(MANAGER_ACCOUNT_CREDENTIALS_INVALID, "Username or password invalid");
    }

    try {
      final AuthenticationManager authenticationManager = BeanLocator.byClassAndName(AuthenticationManager.class, "authenticationManager");
      AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(account, password, account.getAuthorities());
      Authentication successfulAuthentication = authenticationManager.authenticate(authentication);
      LOG.trace("Authenticated successfully: {}", () -> (successfulAuthentication == null ? null : successfulAuthentication.getClass().getSimpleName()));
    } catch (DisabledException de) {
      LOG.warn("{}: {}", () -> de.getClass().getSimpleName(), de::getMessage);
      throw new GeneralManagerException(MANAGER_ACCOUNT_DISABLED, "Account is disabled");
    } catch (LockedException le) {
      LOG.warn("{}: {}", () -> le.getClass().getSimpleName(), le::getMessage);
      throw new GeneralManagerException(MANAGER_ACCOUNT_LOCKED, "Account is locked");
    } catch (BadCredentialsException bce) {
      LOG.warn("{}: {}", () -> bce.getClass().getSimpleName(), bce::getMessage);
      throw new GeneralManagerException(MANAGER_ACCOUNT_CREDENTIALS_INVALID, "Username or password invalid");
    } catch (Exception e) {
      LOG.error(e::getMessage, e);
      throw new RuntimeException("Something really bad happened", e);
    }
  }

  public boolean verify(String verificationCode) throws GeneralManagerException {
    LOG.debug("verificationCode: {}", verificationCode);

    boolean codeFound = false;
    Account account = accountDao.findByVerificationCode(verificationCode);
    LOG.debug("account: {}", () -> (account == null ? null : account.toStringUsingMixIn()));
    if (account != null) {
      LOG.debug("Verify and update account");
      account.setVerified(true)
          .setVerificationCode(null);
      try {
        accountDao.update(account);
      } catch (GeneralDataException de) {
        // re-throw
        throw new GeneralManagerException(de.getKey(), de.getMessage());
      }
      codeFound = true;

      LOG.debug("Sending welcome email");
      try {
        submitSendWelcomeEmailTask(account);
      } catch (Exception e) {
        LOG.error(e::getMessage, e);
        throw new RuntimeException("Something really bad happened", e);
      }
    }

    LOG.debug("Code found: {}", codeFound);
    return codeFound;
  }

  public boolean findPasswordResetCode(String passwordResetCode) {
    LOG.debug("passwordResetCode: {}", passwordResetCode == null ? null : (substring(passwordResetCode, 0, 5) + "..." + substring(passwordResetCode, -5)));

    boolean codeFound = false;
    Account account = accountDao.findByPasswordResetCode(passwordResetCode);
    LOG.debug("account: {}", () -> (account == null ? null : account.toStringUsingMixIn()));
    if (account != null) {
      codeFound = true;
    }

    LOG.debug("Code found: {}", codeFound);
    return codeFound;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    LOG.debug("username: {}", username);
    Account account = findByUsername(username);
    if (account == null) {
      throw new UsernameNotFoundException("Username *not* found: " + username);
    }
    return account;
  }

  public boolean checkEmailAvailable(AccountCheckEmailAvailableRequest request) {
    LOG.debug("request: {}", request);
    return isUsernameAvailable(request.getEmail());
  }

  public Account sendPasswordResetCode(AccountSendPasswordResetCodeRequest request) throws GeneralManagerException {
    LOG.debug("request: {}", request);

    Account account = accountDao.findByUsername(request.getEmail());
    LOG.debug("account: {}", () -> (account == null ? null : account.toStringUsingMixIn()));
    if (account != null) {
      LOG.debug("Set password reset code and update account");
      account.setPasswordResetCode(randomAlphanumeric(PASSWORD_RESET_CODE_LENGTH));
      try {
        accountDao.update(account);
      } catch (GeneralDataException de) {
        // re-throw
        throw new GeneralManagerException(de.getKey(), de.getMessage());
      }

      LOG.debug("Sending password reset email");
      try {
        submitSendResetPasswordEmailTask(account);
      } catch (Exception e) {
        LOG.error(e::getMessage, e);
        throw new RuntimeException("Something really bad happened", e);
      }
    } else {
      throw new GeneralManagerException(CRUD_NOTFOUND, "Account with username [" + request.getEmail() + "] *not* found");
    }

    return account;
  }

  public Account resetPassword(AccountResetPasswordRequest request) throws GeneralManagerException {
    LOG.debug("request: {}", () -> (request == null ? null : request.toStringUsingMixIn()));

    Account account = accountDao.findByPasswordResetCode(request.getPasswordResetCode());
    LOG.debug("account: {}", () -> (account == null ? null : account.toStringUsingMixIn()));
    if (account != null) {
      LOG.debug("Set new password and update account");
      encryptPassword(account, request.getPassword());
      account.setPasswordResetCode(null);
      Account updatedAccount;
      try {
        updatedAccount = accountDao.update(account);
        LOG.debug("account: {}", () -> (updatedAccount == null ? null : updatedAccount.toStringUsingMixIn()));
      } catch (GeneralDataException de) {
        // re-throw
        throw new GeneralManagerException(de.getKey(), de.getMessage());
      }

      LOG.debug("Sending password changed notification email");
      try {
        submitSendNotifyPasswordChangeEmailTask(updatedAccount);
      } catch (Exception e) {
        LOG.error(e::getMessage, e);
        throw new RuntimeException("Something really bad happened", e);
      }
    } else {
      throw new GeneralManagerException(CRUD_NOTFOUND, "Account with passwordResetCode [" + request.getPasswordResetCode() + "] *not* found");
    }

    return account;
  }

  /**
   * Return a "scrubbed" instance of an {@link Account} object
   *
   * @return {@link AccountScrubbed}
   */
  public AccountScrubbed scrub(Account account) {
    return new AccountScrubbed(account);
  }

  /**
   * Return a "cloned" instance of an {@link Account} object
   *
   * @return {@link Account}
   */
  public Account copy(Account account) {
    return new Account(account.getId())
        .setFirstName(account.getFirstName())
        .setLastName(account.getLastName())
        .setUsername(account.getUsername())
        .setTier(account.getTier())
        .setRoles(account.getRoles())
        .setLocale(account.getLocale());
  }

  private void validate(Account account) throws GeneralManagerException {
    if (isBlank(account.getUsername())) {
      throw new GeneralManagerException(CRUD_BADREQUEST, "account.username must *not* be empty");
    }

    String email = trim(lowerCase(account.getUsername()));
    if (!isUsernameAvailable(email)) {
      throw new GeneralManagerException(MANAGER_ACCOUNT_USERNAME_UNAVAILABLE, "Account with username (email) [" + email + "] already exists");
    }
  }

  private void initAccount(Account account, String password) {
    Locale locale = LocaleContextHolder.getLocale();
    LOG.debug("locale: {}", locale);
    if (account != null) {
      String username = trim(lowerCase(account.getUsername()));
      Date createdDate = new Date();
      account.setId()
          .setUsername(username)
          .setEnabled(true)
          .addRoles(USER)
          .setLocale(locale)
          .setCreated(createdDate)
          .setLastModified(createdDate);
      encryptPassword(account, password);
    }
  }

  private void encryptPassword(Account account, String password) {
    if (password != null) {
      long start = System.currentTimeMillis();
      String encryptedPassword = passwordEncoder.encode(password);
      long end = System.currentTimeMillis();
      LOG.debug("{}: password encryption took: {} ms", () -> passwordEncoder.getClass().getSimpleName(), () -> (end - start));
      account.setPassword(encryptedPassword);
    }
    account.setPasswordResetCode(null);
  }

  private boolean isUsernameAvailable(String username) {
    boolean usernameAvailable = false;

    if (username != null) {
      usernameAvailable = true;

      Account account = findByUsername(username);
      if (account != null) {
        LOG.debug("Account with username (email) [{}] already exists", username);
        usernameAvailable = false;
      }
    }

    LOG.debug("Username (email) available: {}", usernameAvailable);
    return usernameAvailable;
  }

  private void submitSendVerifyAccountEmailTask(Account account) {
    Locale locale = account.getLocale();
    String verificationUrl = pageManager.getVerificationUrl(account.getVerificationCode());
    SendVerifyAccountEmailTask task = new SendVerifyAccountEmailTask(locale, account, verificationUrl);
    LOG.debug("Submitting {} with id: {}", () -> task.getClass().getSimpleName(), task::getId);
    //TODO: deal with this future somehow (since task may fail)
    Future<Boolean> taskFuture = asyncTaskExecutor.submit(task);
  }

  private void submitSendNotifyUsernameChangeEmailTask(Account account, Account staleAccount) {
    Locale locale = account.getLocale();
    SendNotifyUsernameChangeEmailTask task = new SendNotifyUsernameChangeEmailTask(locale, account, staleAccount);
    LOG.debug("Submitting {} with id: {}", () -> task.getClass().getSimpleName(), task::getId);
    //TODO: deal with this future somehow (since task may fail)
    Future<Boolean> taskFuture = asyncTaskExecutor.submit(task);
  }

  private void submitSendWelcomeEmailTask(Account account) {
    Locale locale = account.getLocale();
    SendWelcomeEmailTask task = new SendWelcomeEmailTask(locale, account);
    LOG.debug("Submitting {} with id: {}", () -> task.getClass().getSimpleName(), task::getId);
    //TODO: deal with this future somehow (since task may fail)
    Future<Boolean> taskFuture = asyncTaskExecutor.submit(task);
  }

  private void submitSendResetPasswordEmailTask(Account account) throws Exception {
    Locale locale = account.getLocale();
    String passwordResetUrl = pageManager.getPasswordResetUrl(account.getPasswordResetCode());
    SendResetPasswordEmailTask task = new SendResetPasswordEmailTask(locale, account, passwordResetUrl);
    LOG.debug("Submitting {} with id: {}", () -> task.getClass().getSimpleName(), task::getId);
    //TODO: deal with this future somehow (since task may fail)
    Future<Boolean> taskFuture = asyncTaskExecutor.submit(task);
  }

  private void submitSendNotifyPasswordChangeEmailTask(Account account) throws Exception {
    Locale locale = account.getLocale();
    SendNotifyPasswordChangeEmailTask task = new SendNotifyPasswordChangeEmailTask(locale, account);
    LOG.debug("Submitting {} with id: {}", () -> task.getClass().getSimpleName(), task::getId);
    //TODO: deal with this future somehow (since task may fail)
    Future<Boolean> taskFuture = asyncTaskExecutor.submit(task);
  }

  private void checkForNullAccount(Account account) throws GeneralManagerException {
    if (account == null) {
      LOG.warn("account is null");
      throw new GeneralManagerException(CRUD_BADREQUEST, "account is null");
    }
  }

  private void checkForNullAccount(AccountScrubbed accountScrubbed) throws GeneralManagerException {
    if (accountScrubbed == null) {
      LOG.warn("accountScrubbed is null");
      throw new GeneralManagerException(CRUD_BADREQUEST, "account is null");
    }
  }

}
