package com.openease.common.web.security;

import com.openease.common.data.model.account.Account;
import com.openease.common.manager.account.AccountManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * Base authentication security config
 *
 * @author Alan Czajkowski
 */
public abstract class BaseAuthSecurityConfig extends BaseSecurityConfig {

  private static final transient Logger LOG = LogManager.getLogger(BaseAuthSecurityConfig.class);

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AuthenticationEntryPoint authenticationEntryPoint;

  @Autowired
  @Qualifier("messageSource")
  private MessageSource messageSource;

  @Autowired
  private AuthenticationProvider authenticationProvider;

  @Autowired
  private AccountManager accountManager;

  @Override
  protected void init() {
    LOG.debug("Init started");
    super.init();
    LOG.debug("Init finished");
  }

  @Bean({"userDetailsService", "userDetailsServiceBean"})
  @Override
  public UserDetailsService userDetailsServiceBean() throws Exception {
    return userDetailsService();
  }

  @Override
  protected UserDetailsService userDetailsService() {
    return accountManager;
  }

  @Bean({"authenticationManager", "authenticationManagerBean"})
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(authenticationProvider)
        .userDetailsService(accountManager)
        .userDetailsPasswordManager(accountManager)
        .passwordEncoder(passwordEncoder);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    super.configure(http);

    http

        /* Security Management */
        .userDetailsService(accountManager)
          .httpBasic()
          .disable()

        /* Security Exception Handling */
        .exceptionHandling()
          .authenticationEntryPoint(authenticationEntryPoint)
          .and()
    ;
  }

  public static Account getSignedInAccount() {
    Account account = null;

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    LOG.trace("Retrieved authentication in security context: {}", () -> (authentication == null ? null : authentication.getClass().getSimpleName()));
    if (authentication == null) {
      LOG.trace("No authentication in security context");
    } else {
      LOG.debug("{} is of type: {}", Authentication.class::getSimpleName, () -> authentication.getClass().getSimpleName());
      Object principal = authentication.getPrincipal();
      if (principal != null) {
        LOG.debug("{}.principal is of type: {}", () -> authentication.getClass().getSimpleName(), () -> principal.getClass().getSimpleName());
        if (principal instanceof Account) {
          account = (Account) principal;
          LOG.trace("account: {id: [{}], username: [{}]}", account::getId, account::getUsername);
        } else {
          LOG.warn("{}.principal is *not* of type: {}", () -> authentication.getClass().getSimpleName(), Account.class::getSimpleName);
        }
      } else {
        LOG.warn("{}.principal is null", () -> authentication.getClass().getSimpleName());
      }
    }

    return account;
  }

  public static String getSignedInUsername() {
    Account account = getSignedInAccount();
    return account == null
        ? null
        : account.getUsername();
  }

}
