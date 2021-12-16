package com.openease.service.www.manager.account.oauth2;

import com.openease.common.config.Config;
import com.openease.common.data.model.account.OAuth2Provider;
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

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static com.openease.common.web.util.HttpUtils.getCookie;
import static com.openease.service.www.manager.account.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;
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

  @Override
  public void onAuthenticationSuccess(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Authentication authentication) throws IOException, ServletException {
    LOG.debug("HTTP Request: {} {}", httpRequest::getMethod, httpRequest::getRequestURL);

    if (authentication != null) {
      LOG.debug("Authentication is of type: {}", () -> authentication.getClass().getSimpleName());
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
        oAuth2AccountManager.createOrUpdateAccountFromOAuth2User(oAuth2User, oAuth2Provider);
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
      clearAuthenticationAttributes(httpRequest, httpResponse);
      getRedirectStrategy().sendRedirect(httpRequest, httpResponse, targetUrl);
    }
  }

  @Override
  protected String determineTargetUrl(HttpServletRequest httpRequest, HttpServletResponse response, Authentication authentication) {
    Optional<String> redirectUri = getCookie(httpRequest, REDIRECT_URI_PARAM_COOKIE_NAME)
        .map(Cookie::getValue);

    if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
      throw new RuntimeException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
    }

    String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

    //TODO: throw GeneralManagerException
    String jwt = jwtManager.createJwt(authentication);
    if (jwt == null) {
      throw new RuntimeException("Sorry! Unable to create JWT");
    }

    return UriComponentsBuilder.fromUriString(targetUrl)
        .queryParam("token", jwt)
        .build().toUriString();
  }

  protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
    LOG.debug("Clear authentication attributes");
    super.clearAuthenticationAttributes(request);
    httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
  }

  private boolean isAuthorizedRedirectUri(String uri) {
    URI clientRedirectUri = URI.create(uri);

    return config.getAuth().getOAuth2().getAuthorizedRedirectUris()
        .stream()
        .anyMatch(redirectUri -> {
          // only validate host and port, let the clients use different paths if they want to
          URI authorizedRedirectUri = URI.create(redirectUri);
          if (authorizedRedirectUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost()) &&
              authorizedRedirectUri.getPort() == clientRedirectUri.getPort()) {
            return true;
          }
          return false;
        });
  }

}
