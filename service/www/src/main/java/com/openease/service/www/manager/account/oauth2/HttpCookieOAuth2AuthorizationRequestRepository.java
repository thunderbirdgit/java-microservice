package com.openease.service.www.manager.account.oauth2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

import static com.openease.common.util.JavaUtils.deserialize;
import static com.openease.common.util.JavaUtils.serialize;
import static com.openease.common.util.JsonUtils.toJson;
import static com.openease.common.web.util.HttpUtils.addCookie;
import static com.openease.common.web.util.HttpUtils.deleteCookie;
import static com.openease.common.web.util.HttpUtils.getCookie;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * HTTP Cookie OAuth 2.0 {@link AuthorizationRequestRepository}
 *
 * @author Alan Czajkowski
 */
@Component
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

  private static final transient Logger LOG = LogManager.getLogger(HttpCookieOAuth2AuthorizationRequestRepository.class);

  public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
  public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
  private static final int COOKIE_MAX_AGE_SECONDS = (int) Duration.ofMinutes(3).toSeconds();

  @Override
  public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest httpRequest) {
    LOG.debug("HTTP Request: {} {}", httpRequest::getMethod, httpRequest::getRequestURL);
    OAuth2AuthorizationRequest authorizationRequest = getCookie(httpRequest, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
        .map(cookie -> deserialize(cookie.getValue(), OAuth2AuthorizationRequest.class))
        .orElse(null);
    LOG.debug("{}: {}", OAuth2AuthorizationRequest.class::getSimpleName, () -> toJson(authorizationRequest, true));
    return authorizationRequest;
  }

  @Override
  public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    LOG.debug("HTTP Request: {} {}", httpRequest::getMethod, httpRequest::getRequestURL);
    LOG.debug("{}: {}", OAuth2AuthorizationRequest.class::getSimpleName, () -> toJson(authorizationRequest, true));

    if (authorizationRequest == null) {
      removeAuthorizationRequestCookies(httpRequest, httpResponse);
    } else {
      LOG.debug("Adding cookie: {}", () -> OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
      addCookie(httpResponse, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, serialize(authorizationRequest), COOKIE_MAX_AGE_SECONDS);
      String redirectUriAfterLogin = httpRequest.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);
      if (isNotBlank(redirectUriAfterLogin)) {
        LOG.debug("Adding cookie: {}", () -> REDIRECT_URI_PARAM_COOKIE_NAME);
        addCookie(httpResponse, REDIRECT_URI_PARAM_COOKIE_NAME, redirectUriAfterLogin, COOKIE_MAX_AGE_SECONDS);
      }
    }
  }

  @Override
  public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
    return this.loadAuthorizationRequest(request);
  }

  public void removeAuthorizationRequestCookies(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    LOG.debug("Deleting cookie: {}", () -> OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
    deleteCookie(httpRequest, httpResponse, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
    LOG.debug("Deleting cookie: {}", () -> REDIRECT_URI_PARAM_COOKIE_NAME);
    deleteCookie(httpRequest, httpResponse, REDIRECT_URI_PARAM_COOKIE_NAME);
  }

}
