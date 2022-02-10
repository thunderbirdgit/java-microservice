package com.openease.service.image.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Data config
 *
 * @author Alan Czajkowski
 */
@Configuration
//@EnableElasticsearchRepositories({"com.openease.common.data.dao.log"})
//@DependsOn({"elasticsearchEmbeddedServer"})
public class ImageDataConfig {

  private static final transient Logger LOG = LogManager.getLogger(ImageDataConfig.class);

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("Init finished");
  }

}
