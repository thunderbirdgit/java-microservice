package com.openease.service.email.config;

import com.openease.common.web.security.BaseSecurityConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import javax.annotation.PostConstruct;

/**
 * Security config
 *
 * @author Alan Czajkowski
 */
@Configuration
@EnableWebSecurity
@DependsOn({"emailDataConfig", "emailWebConfig"})
public class EmailSecurityConfig extends BaseSecurityConfig {

  private static final transient Logger LOG = LogManager.getLogger(EmailSecurityConfig.class);

  @PostConstruct
  @Override
  public void init() {
    LOG.debug("Init started");
    super.init();
    LOG.debug("Init finished");
  }

}
