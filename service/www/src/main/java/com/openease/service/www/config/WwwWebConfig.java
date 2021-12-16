package com.openease.service.www.config;

import com.openease.common.web.mvc.BaseWebConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.PostConstruct;

/**
 * Web config
 *
 * @author Alan Czajkowski
 */
@Configuration
@EnableWebMvc
@DependsOn({"wwwDataConfig"})
public class WwwWebConfig extends BaseWebConfig {

  private static final transient Logger LOG = LogManager.getLogger(WwwWebConfig.class);

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    super.init();
    LOG.debug("Init finished");
  }

}
