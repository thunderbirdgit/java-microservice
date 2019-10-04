package com.openease.service.payment.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.annotation.PostConstruct;

import static org.springframework.security.config.http.SessionCreationPolicy.ALWAYS;

/**
 * Security config
 *
 * @author Alan Czajkowski
 */
@Configuration
@EnableWebSecurity
@DependsOn({"paymentDataConfig"})
public class PaymentSecurityConfig extends WebSecurityConfigurerAdapter {

  private static final transient Logger LOG = LogManager.getLogger(PaymentSecurityConfig.class);

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("Init finished");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.sessionManagement()
        .sessionCreationPolicy(ALWAYS).and()
        .csrf().disable()
        .headers().frameOptions().sameOrigin();
  }

}
