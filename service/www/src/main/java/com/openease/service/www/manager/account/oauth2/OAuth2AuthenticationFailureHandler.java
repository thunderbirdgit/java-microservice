package com.openease.service.www.manager.account.oauth2;

import com.openease.common.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

import static com.openease.common.web.util.HttpUtils.getCookie;
import static com.openease.service.www.manager.account.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI;
import static com.openease.service.www.manager.account.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_ERROR;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * OAuth 2.0 {@link AuthenticationFailureHandler}
 *
 * @author Alan Czajkowski
 */
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  private static final transient Logger LOG = LogManager.getLogger(OAuth2AuthenticationFailureHandler.class);

  private String defaultTargetUrl;

  @Autowired
  private Config config;

  @Autowired
  private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");

    String defaultTargetUrl = config.getAuth().getOAuth2().getAuthorizedRedirectUris().get(0);
    LOG.debug("default target URL: {}", () -> defaultTargetUrl);
    this.defaultTargetUrl = defaultTargetUrl;

    LOG.debug("Init finished");
  }

  @Override
  public void onAuthenticationFailure(HttpServletRequest httpRequest, HttpServletResponse httpResponse, AuthenticationException exception) throws IOException, ServletException {
    LOG.debug("HTTP Request: {} {}", httpRequest::getMethod, httpRequest::getRequestURL);
    LOG.warn("{}: {}", () -> exception.getClass().getSimpleName(), exception::getMessage);

    String errorMessage = "error";
    String exceptionMessage = exception.getLocalizedMessage();
    if (isNotBlank(exceptionMessage)) {
      errorMessage += ": " + exceptionMessage;
    }

    String redirectUrl = getCookie(httpRequest, REDIRECT_URI)
        .map(Cookie::getValue)
        .orElse(defaultTargetUrl);

    redirectUrl = UriComponentsBuilder.fromUriString(redirectUrl)
        .queryParam(REDIRECT_URI_PARAM_ERROR, URLEncoder.encode(errorMessage, UTF_8))
        .build().toUriString();

    String targetUrl = redirectUrl;
    LOG.debug("target URL: {}", () -> targetUrl);

    if (httpResponse.isCommitted()) {
      LOG.debug("Response has already been committed. Unable to redirect to: {}", () -> targetUrl);
    } else {
      httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(httpRequest, httpResponse);
      getRedirectStrategy().sendRedirect(httpRequest, httpResponse, targetUrl);
    }
  }

}
