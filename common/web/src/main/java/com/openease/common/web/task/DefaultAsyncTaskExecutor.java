package com.openease.common.web.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Default {@link AsyncTaskExecutor}
 *
 * @author Alan Czajkowski
 * @see {@link ThreadPoolTaskExecutor}
 */
@Component("defaultAsyncTaskExecutor")
public class DefaultAsyncTaskExecutor extends ThreadPoolTaskExecutor implements AsyncTaskExecutor {

  private static final transient Logger LOG = LogManager.getLogger(DefaultAsyncTaskExecutor.class);

  private static final String THREAD_NAME_PREFIX = "default-task-executor-";

  @Value("${taskExecutor.corePoolSize}")
  private Integer corePoolSize;

  @Value("${taskExecutor.maxPoolSize}")
  private Integer maxPoolSize;

  @Value("${taskExecutor.queueCapacity}")
  private Integer queueCapacity;

  public DefaultAsyncTaskExecutor() {
    super();
  }

  @PostConstruct
  public void init() {
    LOG.debug("Init started");

    LOG.debug("executor core pool size: {}", corePoolSize);
    LOG.debug("executor max pool size: {}", maxPoolSize);
    LOG.debug("executor queue capacity: {}", queueCapacity);
    LOG.debug("executor thread name prefix: {}", THREAD_NAME_PREFIX);

    setCorePoolSize(corePoolSize);
    setMaxPoolSize(maxPoolSize);
    setQueueCapacity(queueCapacity);
    setThreadNamePrefix(THREAD_NAME_PREFIX);
    setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    initialize();

    LOG.debug("Init finished");
  }

}
