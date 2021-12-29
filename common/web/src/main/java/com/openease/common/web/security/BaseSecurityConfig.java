package com.openease.common.web.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * Base security config
 *
 * @author Alan Czajkowski
 */
public abstract class BaseSecurityConfig extends WebSecurityConfigurerAdapter {

  private static final transient Logger LOG = LogManager.getLogger(BaseSecurityConfig.class);

  protected void init() {
    LOG.debug("Init started");
    LOG.debug("Init finished");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http

        /* Session Management */
        .sessionManagement()
          // sessions are stateless (no cookies), use JWT if needed
          .sessionCreationPolicy(STATELESS)
          .and()

        /* End-point Access */
        .anonymous()
          .and()
        .authorizeRequests()
          .anyRequest()
            .permitAll()
          //TODO: JavaMelody authorization handling
//          .antMatchers("/monitoring")
//            .hasRole(ADMIN.name())
          .and()

        /* Cross-Site Request Forgery (CSRF) */
        .csrf()
          .disable()

        /* HTTP Headers */
        .headers()
          .frameOptions()
          .sameOrigin()
          .and()
    ;
  }

}
