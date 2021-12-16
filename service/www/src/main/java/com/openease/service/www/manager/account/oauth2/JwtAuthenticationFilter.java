package com.openease.service.www.manager.account.oauth2;

import com.openease.common.data.model.account.Account;
import com.openease.common.manager.account.AccountManager;
import com.openease.common.manager.jwt.JwtManager;
import com.openease.common.manager.session.SessionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.openease.common.web.util.HttpUtils.deleteCookie;
import static com.openease.service.www.manager.account.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME;
import static com.openease.service.www.manager.account.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * JSON Web Token (JWT) authentication filter
 *
 * @author Alan Czajkowski
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final transient Logger LOG = LogManager.getLogger(JwtAuthenticationFilter.class);

  public static final String HTTP_HEADER_AUTHORIZATION_PREFIX = "Bearer ";

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
          AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(account, null, account.getAuthorities());
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
          sessionManager.updateSecurityContext(authentication);
        } else {
          LOG.debug("Deleting cookie: {}", () -> OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
          deleteCookie(httpRequest, httpResponse, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
          LOG.debug("Deleting cookie: {}", () -> REDIRECT_URI_PARAM_COOKIE_NAME);
          deleteCookie(httpRequest, httpResponse, REDIRECT_URI_PARAM_COOKIE_NAME);
          sessionManager.updateSecurityContext(null);
        }
      } else {
        LOG.debug("Deleting cookie: {}", () -> OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        deleteCookie(httpRequest, httpResponse, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        LOG.debug("Deleting cookie: {}", () -> REDIRECT_URI_PARAM_COOKIE_NAME);
        deleteCookie(httpRequest, httpResponse, REDIRECT_URI_PARAM_COOKIE_NAME);
        sessionManager.updateSecurityContext(null);
      }
    } catch (Exception e) {
      LOG.error("Could not set user authentication in security context", e);
    }

    chain.doFilter(httpRequest, httpResponse);
  }

  private String getJwtFromHttpRequest(HttpServletRequest httpRequest) {
    String jwt = null;

    String bearerToken = httpRequest.getHeader(AUTHORIZATION);
    if (startsWithIgnoreCase(bearerToken, HTTP_HEADER_AUTHORIZATION_PREFIX)) {
      jwt = bearerToken.substring(HTTP_HEADER_AUTHORIZATION_PREFIX.length());
    }

    return jwt;
  }

}
