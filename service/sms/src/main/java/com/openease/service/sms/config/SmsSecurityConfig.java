package com.openease.service.sms.config;

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
@DependsOn({"smsDataConfig", "smsWebConfig"})
public class SmsSecurityConfig extends BaseSecurityConfig {

  private static final transient Logger LOG = LogManager.getLogger(SmsSecurityConfig.class);

  @PostConstruct
  @Override
  public void init() {
    LOG.debug("Init started");
    super.init();
    LOG.debug("Init finished");
  }

}
