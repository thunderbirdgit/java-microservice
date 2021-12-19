package com.openease.common.web.filter;

import com.openease.common.data.model.account.Account;
import com.openease.common.manager.account.AccountManager;
import com.openease.common.manager.jwt.JwtManager;
import com.openease.common.manager.session.SessionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * JSON Web Token (JWT) authentication filter
 *
 * @author Alan Czajkowski
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final transient Logger LOG = LogManager.getLogger(JwtAuthenticationFilter.class);

  public static final String HTTP_HEADER_AUTHORIZATION_BEARER_PREFIX = "Bearer ";

  @Autowired
  private JwtManager jwtManager;

  @Autowired
  private AccountManager accountManager;

  @Autowired
  private SessionManager sessionManager;

  @Override
  protected void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain chain) throws ServletException, IOException {
    LOG.debug("HTTP Request: {} {}", httpRequest::getMethod, httpRequest::getRequestURL);

    try {
      String jwt = getJwtFromHttpRequest(httpRequest);
      LOG.trace("JWT from HTTP request: {}", () -> jwt);

      if (jwtManager.validateJwt(jwt)) {
        LOG.debug("JWT is valid");
        String username = jwtManager.getUsernameFromJwt(jwt);
        LOG.debug("JWT contains username: {}", () -> username);

        Account account = accountManager.findByUsername(username);
        LOG.debug("account: {}", () -> (account == null ? null : account.toStringUsingMixIn()));
        if (account != null) {
          LOG.debug("Update security context with new authentication");
          AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(account, null, account.getAuthorities());
          //TODO: authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
          LOG.trace("Updating authentication in security context: {}", () -> (authentication == null ? null : authentication.getClass().getSimpleName()));
          SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
          LOG.trace("Updating authentication in security context: null");
          SecurityContextHolder.getContext().setAuthentication(null);
        }
      } else {
        LOG.trace("Updating authentication in security context: null");
        SecurityContextHolder.getContext().setAuthentication(null);
      }
    } catch (Exception e) {
      LOG.error("Could not set user authentication in security context", e);
    }

    chain.doFilter(httpRequest, httpResponse);
  }

  private String getJwtFromHttpRequest(HttpServletRequest httpRequest) {
    String jwt = null;

    String authorizationHeaderValue = httpRequest.getHeader(AUTHORIZATION);
    if (startsWithIgnoreCase(authorizationHeaderValue, HTTP_HEADER_AUTHORIZATION_BEARER_PREFIX)) {
      jwt = authorizationHeaderValue.substring(HTTP_HEADER_AUTHORIZATION_BEARER_PREFIX.length());
    }

    return jwt;
  }

}
