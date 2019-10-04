package com.openease.service.email.config;

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
@EnableElasticsearchRepositories({"com.openease.common.data.dao.log"})
@DependsOn({"elasticsearchEmbeddedServer"})
public class EmailDataConfig {

  private static final transient Logger LOG = LogManager.getLogger(EmailDataConfig.class);

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("Init finished");
  }

}
