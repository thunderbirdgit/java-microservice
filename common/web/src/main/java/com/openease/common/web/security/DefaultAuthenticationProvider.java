package com.openease.common.web.security;

import com.openease.common.manager.account.AccountManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component("authenticationProvider")
public class DefaultAuthenticationProvider extends DaoAuthenticationProvider {

  private static final transient Logger LOG = LogManager.getLogger(DefaultAuthenticationProvider.class);

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AccountManager accountManager;

  @Autowired
  @Qualifier("messageSource")
  private MessageSource messageSource;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");

    this.setPasswordEncoder(passwordEncoder);
    this.setUserDetailsService(accountManager);
    this.setUserDetailsPasswordService(accountManager);
    this.setMessageSource(messageSource);

    LOG.debug("Init finished");
  }

}
