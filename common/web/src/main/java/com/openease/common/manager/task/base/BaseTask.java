package com.openease.common.manager.task.base;

import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Base task
 *
 * @author Alan Czajkowski
 */
public abstract class BaseTask<R> implements Callable<R> {

  protected final UUID id;

  public BaseTask() {
    this.id = UUID.randomUUID();
  }

  protected abstract R execute();

  protected abstract void logStart();

  protected abstract void logFinish();

  @Override
  public R call() throws Exception {
    logStart();
    R result = execute();
    logFinish();
    return result;
  }

  protected String getLogStartMessage() {
    return "[" + id + "] task started";
  }

  protected String getLogFinishMessage() {
    return "[" + id + "] task finished";
  }

  public UUID getId() {
    return id;
  }

}
