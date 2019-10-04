package com.openease.service.www.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import javax.annotation.PostConstruct;

/**
 * Data config
 *
 * @author Alan Czajkowski
 */
@Configuration
@EnableElasticsearchRepositories({"com.openease.common.data.dao.*"})
@DependsOn({"elasticsearchEmbeddedServer"})
public class WwwDataConfig {

  private static final transient Logger LOG = LogManager.getLogger(WwwDataConfig.class);

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("Init finished");
  }

}
