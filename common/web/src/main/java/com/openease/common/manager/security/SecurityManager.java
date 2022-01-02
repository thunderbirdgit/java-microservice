package com.openease.common.manager.security;

import com.openease.common.data.model.account.Account;
import com.openease.common.manager.account.AccountManager;
import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.jwt.JwtManager;
import com.openease.common.manager.security.request.SecurityCreateAuthRequest;
import com.openease.common.manager.security.response.SecurityCreateAuthResponse;
import com.openease.common.web.security.BaseAuthSecurityConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.openease.common.data.lang.MessageKeys.CRUD_BADREQUEST;
import static com.openease.common.web.security.BaseAuthSecurityConfig.getSignedInUsername;

/**
 * Security manager
 *
 * @author Alan Czajkowski
 */
@Service
public class SecurityManager {

  private static final transient Logger LOG = LogManager.getLogger(SecurityManager.class);

  public static final String SECURITY = "security";

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
   * Create security authentication (sign-in)
   *
   * @param request {@link SecurityCreateAuthRequest}
   *
   * @return {@link SecurityCreateAuthResponse}
   *
   * @throws GeneralManagerException
   */
  public SecurityCreateAuthResponse create(SecurityCreateAuthRequest request) throws GeneralManagerException {
    LOG.debug("request: {}", () -> (request == null ? null : request.toStringUsingMixIn()));
    if (request == null) {
      LOG.warn("request is null");
      throw new GeneralManagerException(CRUD_BADREQUEST, "request is null");
    }

    SecurityCreateAuthResponse response;

    LOG.debug("Signing-in account username: [{}]", request::getUsername);
    Account account = accountManager.findByUsername(request.getUsername());
    accountManager.verifyPassword(account, request.getPassword());

    LOG.debug("Update security context with new authentication");
    AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(account, null, account.getAuthorities());
    //TODO: authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
    LOG.trace("Updating authentication in security context: {}", () -> (authentication == null ? null : authentication.getClass().getSimpleName()));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    LOG.debug("Creating JWT for account username: [{}]", account::getUsername);
    String jwt = jwtManager.createJwt(authentication);
    if (jwt == null) {
      throw new RuntimeException("Sorry! Unable to create JWT");
    }

    LOG.debug("Signed-in account username: [{}]", BaseAuthSecurityConfig::getSignedInUsername);

    // set response
    response = new SecurityCreateAuthResponse()
        .setJwt(jwt)
        .setAccountId(account.getId());

    LOG.debug("response: {}", response);
    return response;
  }

  /**
   * Delete security authentication (sign-out)
   */
  public void delete() {
    LOG.debug("Signing-out account username: [{}]", BaseAuthSecurityConfig::getSignedInUsername);
    LOG.trace("Updating authentication in security context: null");
    SecurityContextHolder.getContext().setAuthentication(null);
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

}
