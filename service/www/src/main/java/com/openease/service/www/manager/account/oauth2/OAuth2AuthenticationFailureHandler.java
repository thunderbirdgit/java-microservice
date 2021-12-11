package com.openease.service.www.manager.account.oauth2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.openease.common.web.util.HttpUtils.getCookie;
import static com.openease.service.www.manager.account.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

/**
 * OAuth 2.0 {@link AuthenticationFailureHandler}
 *
 * @author Alan Czajkowski
 */
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  private static final transient Logger LOG = LogManager.getLogger(OAuth2AuthenticationFailureHandler.class);

  @Autowired
  private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

  @Override
  public void onAuthenticationFailure(HttpServletRequest httpRequest, HttpServletResponse httpResponse, AuthenticationException exception) throws IOException, ServletException {
    LOG.debug("HTTP Request: {} {}", httpRequest::getMethod, httpRequest::getRequestURL);
    LOG.warn("{}: {}", () -> exception.getClass().getSimpleName(), exception::getMessage);

    String redirectUrl = getCookie(httpRequest, REDIRECT_URI_PARAM_COOKIE_NAME)
        .map(Cookie::getValue)
        .orElse(("/"));

    redirectUrl = UriComponentsBuilder.fromUriString(redirectUrl)
        .queryParam("error", exception.getLocalizedMessage())
        .build().toUriString();

    httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(httpRequest, httpResponse);

    getRedirectStrategy().sendRedirect(httpRequest, httpResponse, redirectUrl);
  }

}
