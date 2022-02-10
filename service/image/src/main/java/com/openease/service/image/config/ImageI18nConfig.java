package com.openease.service.image.config;

import com.openease.common.web.lang.BaseI18nConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PostConstruct;

/**
 * Internationalization (i18n) config
 *
 * @author Alan Czajkowski
 */
@Configuration
@DependsOn({"imageDataConfig"})
public class ImageI18nConfig extends BaseI18nConfig {

  private static final transient Logger LOG = LogManager.getLogger(ImageI18nConfig.class);

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    super.init();
    LOG.debug("Init finished");
  }

}
