package com.openease.service.sms.config;

import com.openease.common.web.task.BaseTaskConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

/**
 * Task config
 *
 * @author Alan Czajkowski
 */
@Configuration
@EnableAsync
@EnableScheduling
@DependsOn({"smsDataConfig"})
public class SmsTaskConfig extends BaseTaskConfig {

  private static final transient Logger LOG = LogManager.getLogger(SmsTaskConfig.class);

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    super.init();
    LOG.debug("Init finished");
  }

}
