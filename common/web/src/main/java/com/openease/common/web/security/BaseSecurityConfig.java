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
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

import static org.springframework.security.config.http.SessionCreationPolicy.ALWAYS;

/**
 * Base security config
 *
 * @author Alan Czajkowski
 */
public abstract class BaseSecurityConfig extends WebSecurityConfigurerAdapter {

  private static final transient Logger LOG = LogManager.getLogger(BaseSecurityConfig.class);

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AuthenticationEntryPoint authenticationEntryPoint;

  @Autowired
  private AccountManager accountManager;

  @Autowired
  @Qualifier("messageSource")
  private MessageSource messageSource;

  @Autowired
  private AuthenticationProvider authenticationProvider;

  public void init() {
    // do nothing
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

  @Bean({
      "authenticationProvider", "authenticationProviderBean",
      "daoAuthenticationProvider", "daoAuthenticationProviderBean"
  })
  public AuthenticationProvider authenticationProviderBean() {
    DaoAuthenticationProvider daoAuthProvider = new DaoAuthenticationProvider();
    daoAuthProvider.setPasswordEncoder(passwordEncoder);
    daoAuthProvider.setUserDetailsService(accountManager);
    daoAuthProvider.setMessageSource(messageSource);
    return daoAuthProvider;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(authenticationProvider);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.userDetailsService(accountManager)

        // session handling
        .sessionManagement()
        .sessionCreationPolicy(ALWAYS)

        .and()

        // security exception handling
        .exceptionHandling()
        .authenticationEntryPoint(authenticationEntryPoint)

        .and()

        //TODO: JavaMelody authorization handling
//        .authorizeRequests().antMatchers("/monitoring").hasRole(ADMIN.name())
//        .anyRequest().permitAll().and()
//        .anonymous().disable()

        .csrf().disable()
        .headers().frameOptions().sameOrigin();
  }

  public static Account getSignedInAccount() {
    Account account = null;

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      LOG.trace("No authentication token in session");
    } else {
      Object principal = authentication.getPrincipal();
      if (principal != null) {
        if (principal instanceof Account) {
          account = (Account) principal;
          LOG.trace("account{id: [{}], username: [{}]}", account::getId, account::getUsername);
        } else {
          LOG.warn("Authentication principal is *not* of type {}", Account.class::getSimpleName);
        }
      } else {
        LOG.warn("Authentication principal is *null*");
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
