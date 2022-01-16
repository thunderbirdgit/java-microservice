package com.openease.common.web.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Default {@link PasswordEncoder}
 *
 * @author Alan Czajkowski
 * @see {@link BCryptPasswordEncoder}
 */
@Component("passwordEncoder")
public class DefaultPasswordEncoder extends BCryptPasswordEncoder implements PasswordEncoder {

  private static final transient Logger LOG = LogManager.getLogger(DefaultPasswordEncoder.class);

  /**
   * @see {@link BCryptPasswordEncoder}
   */
  private static final int BCRYPT_PASSWORD_ENCODER_STRENGTH = 13;

  public DefaultPasswordEncoder() {
    super(BCRYPT_PASSWORD_ENCODER_STRENGTH);
  }

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("{} strength: {}", BCryptPasswordEncoder.class::getSimpleName, () -> BCRYPT_PASSWORD_ENCODER_STRENGTH);
    LOG.debug("Init finished");
  }

}
