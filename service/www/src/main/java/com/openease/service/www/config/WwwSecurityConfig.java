package com.openease.service.www.config;

import com.openease.common.web.security.BaseSecurityConfig;
import com.openease.service.www.manager.account.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.openease.service.www.manager.account.oauth2.OAuth2AccountManager;
import com.openease.service.www.manager.account.oauth2.OAuth2AuthenticationFailureHandler;
import com.openease.service.www.manager.account.oauth2.OAuth2AuthenticationSuccessHandler;
import com.openease.service.www.manager.account.oauth2.JwtAuthenticationFilter;
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

import javax.annotation.PostConstruct;

import static org.springframework.http.HttpMethod.DELETE;

/**
 * Security config
 *
 * @author Alan Czajkowski
 */
@Configuration
@EnableWebSecurity
@DependsOn({"wwwDataConfig"})
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WwwSecurityConfig extends BaseSecurityConfig {

  private static final transient Logger LOG = LogManager.getLogger(WwwSecurityConfig.class);

  @Autowired
  private OAuth2AccountManager oAuth2AccountManager;

  @Autowired
  private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

  @Autowired
  private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

  @Autowired
  private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

  @PostConstruct
  @Override
  public void init() {
    LOG.debug("Init started");
    super.init();
    LOG.debug("Init finished");
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

//        .authorizeRequests()
//          .antMatchers(
//              /* welcome pages */
//              "/",
//              "/index.html",
//              /* end-points */
//              "/info/**",
//              "/enum/**",
//              "/v1/**",
//              /* pages */
//              "/error", "/error/",
//              "/pages", "/pages/",
//              "/templates", "/templates/",
//              /* static assets */
//              "/**/static/**",
//              "/**/js/**",
//              "/**/css/**",
//              "/**/img/**",
//              "/**/media/**",
//              "/**/*.ico",
//              /* auth */
//              "/sign-in.html",
//              "/sessions/**"
//          )
//            .permitAll()
//          .anyRequest()
//            .authenticated()
//          .and()

        /* OAuth 2.0 */
        .oauth2Login()
          .loginPage("/")
//          .loginProcessingUrl()
          .authorizationEndpoint()
            .baseUri("/sessions/oauth2/authorize")
            .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
          .and()
          .redirectionEndpoint()
            .baseUri("/sessions/oauth2/callback/*")
          .and()
          .userInfoEndpoint()
            .userService(oAuth2AccountManager)
          .and()
          .successHandler(oAuth2AuthenticationSuccessHandler)
          .failureHandler(oAuth2AuthenticationFailureHandler)
          .and()

        /* Sign Out */
        .logout()
          .logoutRequestMatcher(new AntPathRequestMatcher("/sessions", DELETE.name()))
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
   * By default, Spring OAuth 2.0 uses {@link HttpSessionOAuth2AuthorizationRequestRepository}
   * to save the Authorization request, but since our service is stateless, we cannot
   * save it in the session. We will save the request in a Base64-encoded cookie instead.
   */
  @Bean
  public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
    return new HttpCookieOAuth2AuthorizationRequestRepository();
  }

}
