package com.openease.common.manager.session;

import com.openease.common.data.model.account.Account;
import com.openease.common.manager.account.AccountManager;
import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.jwt.JwtManager;
import com.openease.common.manager.session.request.SessionCreateRequest;
import com.openease.common.manager.session.response.SessionCreateResponse;
import com.openease.common.web.security.BaseAuthSecurityConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.openease.common.data.lang.MessageKeys.CRUD_BADREQUEST;
import static com.openease.common.web.security.BaseAuthSecurityConfig.getSignedInUsername;

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

  @Autowired
  private JwtManager jwtManager;

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

    LOG.debug("Signing-in account username: [{}]", request::getUsername);
    Account account = accountManager.findByUsername(request.getUsername());
    Authentication authentication = accountManager.verifyPassword(account, request.getPassword());
    updateSecurityContext(authentication);

    LOG.debug("Creating JWT for account username: [{}]", account::getUsername);
    //TODO: throw GeneralManagerException
    String jwt = jwtManager.createJwt(authentication);
    if (jwt == null) {
      throw new RuntimeException("Sorry! Unable to create JWT");
    }

    LOG.debug("Signed-in account username: [{}]", BaseAuthSecurityConfig::getSignedInUsername);

    // set response
    response = new SessionCreateResponse()
        .setAccountId(account.getId())
        .setJwt(jwt);

    LOG.debug("response: {}", response);
    return response;
  }

  /**
   * Delete session (sign-out)
   */
  public void delete() {
    LOG.debug("Signing-out account username: [{}]", BaseAuthSecurityConfig::getSignedInUsername);
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
    LOG.trace("Setting authentication in security context: {}", () -> (authentication == null ? null : authentication.getClass().getSimpleName()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

}
