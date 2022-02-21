package com.openease.common.manager.page;

import com.openease.common.config.Config;
import com.openease.common.manager.account.request.AccountResetPasswordRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Page manager
 *
 * @author Alan Czajkowski
 */
@Service
public class PageManager {

  private static final transient Logger LOG = LogManager.getLogger(PageManager.class);

  public static final String PAGES = "pages";
  public static final String PAGES_CONTEXT = "/" + PAGES;

  public static final String PASSWORDRESET_CONTEXT = "/password-reset";
  public static final String PASSWORDRESET_CONTEXT_ABSOLUTE = PAGES_CONTEXT + PASSWORDRESET_CONTEXT;

  public static final String VERIFICATION_CONTEXT = "/verification";
  public static final String VERIFICATION_CONTEXT_ABSOLUTE = PAGES_CONTEXT + VERIFICATION_CONTEXT;

  @Autowired
  private Config config;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("Init finished");
  }

  public String getVerificationUrl(String verificationCode) {
    return config.getFullyQualifiedUrl(VERIFICATION_CONTEXT_ABSOLUTE + "/" + verificationCode);
  }

  public String getPasswordResetUrl(String passwordResetCode) {
    return config.getFullyQualifiedUrl(PASSWORDRESET_CONTEXT_ABSOLUTE + "/" + passwordResetCode);
  }

  public AccountResetPasswordRequest createAccountResetPasswordRequest(String passwordResetCode) {
    return new AccountResetPasswordRequest().setPasswordResetCode(passwordResetCode);
  }

}
