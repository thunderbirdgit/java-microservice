package com.openease.service.www.config;

import com.openease.common.config.Config;
import com.openease.common.web.filter.JwtAuthenticationFilter;
import com.openease.common.web.security.BaseAuthSecurityConfig;
import com.openease.service.www.api.security.SecurityController;
import com.openease.service.www.manager.account.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.openease.service.www.manager.account.oauth2.OAuth2AccountManager;
import com.openease.service.www.manager.account.oauth2.OAuth2AuthenticationFailureHandler;
import com.openease.service.www.manager.account.oauth2.OAuth2AuthenticationSuccessHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.PostConstruct;

import static com.openease.common.util.JsonUtils.toJson;
import static org.springframework.http.HttpMethod.DELETE;

/**
 * Security config
 *
 * @author Alan Czajkowski
 */
@Configuration
@EnableWebSecurity
@DependsOn({"wwwDataConfig", "wwwWebConfig"})
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WwwSecurityConfig extends BaseAuthSecurityConfig {

  private static final transient Logger LOG = LogManager.getLogger(WwwSecurityConfig.class);

  @Autowired
  private Config config;

  @Autowired
  private OAuth2AccountManager oAuth2AccountManager;

  @Autowired
  private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

  @Autowired
  private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

  @Autowired
  private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

  @Autowired
  private SecurityController securityController;

  @PostConstruct
  @Override
  public void init() {
    LOG.debug("Init started");
    super.init();

    LOG.debug("CORS allowed origins: {}", () -> toJson(config.getCors().getAllowedOrigins()));
    LOG.debug("CORS allowed origin patterns: {}", () -> toJson(config.getCors().getAllowedOriginPatterns()));
    LOG.debug("CORS allowed methods: {}", () -> toJson(config.getCors().getAllowedMethods()));
    LOG.debug("CORS allowed headers: {}", () -> toJson(config.getCors().getAllowedHeaders()));
    LOG.debug("CORS exposed headers: {}", () -> toJson(config.getCors().getExposedHeaders()));
    LOG.debug("CORS allow credentials: {}", () -> config.getCors().isAllowCredentials());
    LOG.debug("CORS max age (seconds): {}", () -> config.getCors().getMaxAgeSeconds());

    LOG.debug("Init finished");
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.setAllowedOrigins(config.getCors().getAllowedOrigins());
    corsConfig.setAllowedOriginPatterns(config.getCors().getAllowedOriginPatterns());
    corsConfig.setAllowedMethods(config.getCors().getAllowedMethods());
    corsConfig.setAllowedHeaders(config.getCors().getAllowedHeaders());
    corsConfig.setExposedHeaders(config.getCors().getExposedHeaders());
    corsConfig.setAllowCredentials(config.getCors().isAllowCredentials());
    corsConfig.setMaxAge(config.getCors().getMaxAgeSeconds());

    UrlBasedCorsConfigurationSource corsConfigSource = new UrlBasedCorsConfigurationSource();
    corsConfigSource.registerCorsConfiguration("/**", corsConfig);

    return corsConfigSource;
  }

  @Bean
  public CorsFilter corsFilter() {
    return new CorsFilter(corsConfigurationSource()) {
      @PostConstruct
      public void init() {
        LOG.debug("Init started");
        LOG.debug("Init finished");
      }
    };
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    super.configure(http);

    http

        /* Cross-Origin Resource Sharing (CORS) */
        .cors()
          .and()

        .formLogin()
          .disable()

        .httpBasic()
          .disable()

        /* OAuth 2.0 */
        .oauth2Login()
          .loginPage("/")
//          .loginProcessingUrl()
          .authorizationEndpoint()
            .baseUri("/security/auth/oauth2/authorize")
            .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
          .and()
          .redirectionEndpoint()
            .baseUri("/security/auth/oauth2/callback/*")
          .and()
          .userInfoEndpoint()
            .userService(oAuth2AccountManager)
//TODO:            .oidcUserService(oAuth2AccountManager)
          .and()
          .successHandler(oAuth2AuthenticationSuccessHandler)
          .failureHandler(oAuth2AuthenticationFailureHandler)
          .and()

        /* Sign Out */
        .logout()
          .addLogoutHandler(securityController)
          .logoutRequestMatcher(new AntPathRequestMatcher("/security/auth", DELETE.name()))
          .logoutSuccessUrl("/")
          .and()

        /* JWT Authentication Filter */
        .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
    ;
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter();
  }

  /**
   * By default, Spring Security OAuth 2.0 uses {@link HttpSessionOAuth2AuthorizationRequestRepository}
   * to save the Authorization request, but since our service is stateless, we cannot
   * save it in the session. We will save the request in a Base64-encoded cookie instead.
   */
  @Bean
  public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
    return new HttpCookieOAuth2AuthorizationRequestRepository();
  }

}
