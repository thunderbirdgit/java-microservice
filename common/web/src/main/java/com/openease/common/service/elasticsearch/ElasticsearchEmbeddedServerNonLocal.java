package com.openease.common.service.elasticsearch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static com.openease.common.Env.Constants.LOCAL;
import static com.openease.common.Env.Constants.NOT;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 * Elasticsearch embedded server (non-local)
 *
 * @author Alan Czajkowski
 */
@Service("elasticsearchEmbeddedServer")
@Order(HIGHEST_PRECEDENCE)
@Profile({NOT + LOCAL})
public class ElasticsearchEmbeddedServerNonLocal implements ElasticsearchEmbeddedServer {

  private static final transient Logger LOG = LogManager.getLogger(ElasticsearchEmbeddedServerNonLocal.class);

  /**
   * {@link InitializingBean#afterPropertiesSet} needs to be used
   * instead of {@link PostConstruct}
   *
   * @throws Exception
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    start();
  }

  @Override
  public void start() {
    LOG.warn("Application started in non-{} mode ... skipping", LOCAL);
  }

  @PreDestroy
  @Override
  public void stop() {
    // do nothing
  }

}
