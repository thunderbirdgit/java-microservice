package com.openease.service.www.manager.account.oauth2;

import com.openease.common.config.Config;
import com.openease.common.data.model.account.OAuth2Provider;
import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.jwt.JwtManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Optional;

import static com.openease.common.web.util.HttpUtils.getCookie;
import static com.openease.service.www.manager.account.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI;
import static com.openease.service.www.manager.account.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_TOKEN;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.upperCase;

/**
 * OAuth 2.0 {@link AuthenticationSuccessHandler}
 *
 * @author Alan Czajkowski
 */
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private static final transient Logger LOG = LogManager.getLogger(OAuth2AuthenticationSuccessHandler.class);

  @Autowired
  private Config config;

  @Autowired
  private OAuth2AccountManager oAuth2AccountManager;

  @Autowired
  private JwtManager jwtManager;

  @Autowired
  private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");

    String defaultTargetUrl = config.getAuth().getOAuth2().getAuthorizedRedirectUris().get(0);
    LOG.debug("default target URL: {}", () -> defaultTargetUrl);
    setDefaultTargetUrl(defaultTargetUrl);

    LOG.debug("Init finished");
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Authentication authentication) throws IOException, ServletException {
    LOG.debug("HTTP Request: {} {}", httpRequest::getMethod, httpRequest::getRequestURL);

    if (authentication != null) {
      LOG.debug("{} is of type: {}", Authentication.class::getSimpleName, () -> authentication.getClass().getSimpleName());
      if (authentication instanceof OAuth2AuthenticationToken) {
        OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) authentication;

        String oAuth2ProviderId = authenticationToken.getAuthorizedClientRegistrationId();
        LOG.debug("OAuth 2.0 provider: {}", () -> oAuth2ProviderId);
        OAuth2Provider oAuth2Provider;
        try {
          oAuth2Provider = OAuth2Provider.valueOf(upperCase(oAuth2ProviderId));
        } catch (IllegalArgumentException iae) {
          LOG.error("OAuth 2.0 sign-in provider not recognized: {}", () -> oAuth2ProviderId);
          throw new OAuth2AuthenticationException("OAuth 2.0 sign-in provider not recognized: " + oAuth2ProviderId);
        }

        OAuth2User oAuth2User = authenticationToken.getPrincipal();
        oAuth2AccountManager.createOrUpdateAccountFromOAuth2User(httpRequest, oAuth2User, oAuth2Provider);
      } else {
        LOG.error("Authentication is *not* of type: {}", OAuth2AuthenticationToken.class::getSimpleName);
        throw new OAuth2AuthenticationException("Authentication is *not* of type: " + OAuth2AuthenticationToken.class.getSimpleName());
      }
    } else {
      LOG.error("Authentication is null");
      throw new OAuth2AuthenticationException("Authentication is null");
    }

    String targetUrl = determineTargetUrl(httpRequest, httpResponse, authentication);
    LOG.debug("target URL: {}", () -> targetUrl);

    if (httpResponse.isCommitted()) {
      LOG.debug("Response has already been committed. Unable to redirect to: {}", () -> targetUrl);
    } else {
      LOG.debug("Clear authentication attributes");
      super.clearAuthenticationAttributes(httpRequest);
      httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(httpRequest, httpResponse);
      getRedirectStrategy().sendRedirect(httpRequest, httpResponse, targetUrl);
    }
  }

  @Override
  protected String determineTargetUrl(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Authentication authentication) {
    Optional<String> redirectUri = getCookie(httpRequest, REDIRECT_URI)
        .map(Cookie::getValue);

    if (redirectUri.isPresent() && !isRedirectUriAuthorized(redirectUri.get())) {
      LOG.error("Unauthorized redirect URI: {}", redirectUri::get);
      throw new OAuth2AuthenticationException("Unauthorized redirect URI");
    }

    String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

    String jwt;
    try {
      jwt = jwtManager.createJwt(authentication);
    } catch (GeneralManagerException me) {
      LOG.error(me::getMessage, me);
      throw new OAuth2AuthenticationException(me.getMessage());
    }

    return UriComponentsBuilder.fromUriString(targetUrl)
        .queryParam(REDIRECT_URI_PARAM_TOKEN, URLEncoder.encode(jwt, UTF_8))
        .build().toUriString();
  }

  private boolean isRedirectUriAuthorized(String uri) {
    boolean authorized = false;

    LOG.trace("Verify redirect URI is authorized: {}", () -> uri);
    if (isNotBlank(uri)) {
      URI clientRedirectUri = URI.create(uri);
      authorized = config.getAuth().getOAuth2().getAuthorizedRedirectUris()
          .stream()
          .anyMatch(redirectUri -> {
            LOG.trace("Match against: {}", () -> redirectUri);
            // validate only host and port (let client use different path if desired)
            URI authorizedRedirectUri = URI.create(redirectUri);
            LOG.trace("... scheme: {}", authorizedRedirectUri::getScheme);
            LOG.trace("... host: {}", authorizedRedirectUri::getHost);
            LOG.trace("... port: {}", authorizedRedirectUri::getPort);
            return equalsIgnoreCase(authorizedRedirectUri.getScheme(), clientRedirectUri.getScheme())
                && equalsIgnoreCase(authorizedRedirectUri.getHost(), clientRedirectUri.getHost())
                && authorizedRedirectUri.getPort() == clientRedirectUri.getPort();
          });
    }
    LOG.trace("Redirect URI is authorized: {}", authorized);

    return authorized;
  }

}
