package com.openease.service.www.config;

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
@DependsOn({"wwwDataConfig"})
public class WwwTaskConfig extends BaseTaskConfig {

  private static final transient Logger LOG = LogManager.getLogger(WwwTaskConfig.class);

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    super.init();
    LOG.debug("Init finished");
  }

}
