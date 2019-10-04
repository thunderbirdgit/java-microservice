package com.openease.common.web.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.util.concurrent.Executor;

/**
 * Base task config
 *
 * @author Alan Czajkowski
 */
public abstract class BaseTaskConfig implements AsyncConfigurer {

  @Autowired
  @Qualifier("defaultAsyncTaskExecutor")
  private AsyncTaskExecutor asyncTaskExecutor;

  public void init() {
    // do nothing
  }

  @Override
  public Executor getAsyncExecutor() {
    return asyncTaskExecutor;
  }

}
