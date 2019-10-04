package com.openease.common.manager.session;

import com.openease.common.data.model.account.Account;
import com.openease.common.manager.account.AccountManager;
import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.session.request.SessionCreateRequest;
import com.openease.common.manager.session.response.SessionCreateResponse;
import com.openease.common.web.security.BaseSecurityConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.openease.common.data.lang.MessageKeys.CRUD_BADREQUEST;
import static com.openease.common.web.security.BaseSecurityConfig.getSignedInUsername;

/**
 * Session manager
 *
 * @author Alan Czajkowski
 */
@Service
public class SessionManager {

  private static final transient Logger LOG = LogManager.getLogger(SessionManager.class);

  public static final String SESSIONS = "sessions";

  @Autowired
  private AccountManager accountManager;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("Init finished");
  }

  /**
   * Create session (sign-in)
   *
   * @param request {@link SessionCreateRequest}
   *
   * @return {@link SessionCreateResponse}
   *
   * @throws GeneralManagerException
   */
  public SessionCreateResponse create(SessionCreateRequest request) throws GeneralManagerException {
    LOG.debug("request: {}", () -> (request == null ? null : request.toStringUsingMixIn()));
    if (request == null) {
      LOG.warn("request is null");
      throw new GeneralManagerException(CRUD_BADREQUEST, "request is null");
    }

    SessionCreateResponse response;

    Account account = accountManager.findByUsername(request.getUsername());
    Authentication authentication = accountManager.verifyPassword(account, request.getPassword());
    updateSecurityContext(authentication);

    LOG.debug("Signed-in account username: [{}]", BaseSecurityConfig::getSignedInUsername);

    // set response
    response = new SessionCreateResponse()
        .setAccountId(account.getId());

    LOG.debug("response: {}", response);
    return response;
  }

  /**
   * Delete session (sign-out)
   */
  public void delete() {
    LOG.debug("Signing out account username: [{}]", BaseSecurityConfig::getSignedInUsername);

    // destroy authentication
    updateSecurityContext(null);
  }

  public Account getSignedInAccount() {
    String username = getSignedInUsername();
    return accountManager.findByUsername(username);
  }

  public String getSignedInAccountId() {
    Account account = getSignedInAccount();
    return account == null
        ? null
        : account.getId();
  }

  public void updateSecurityContext(Authentication authentication) {
    LOG.trace("Setting authentication token in session");
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

}
