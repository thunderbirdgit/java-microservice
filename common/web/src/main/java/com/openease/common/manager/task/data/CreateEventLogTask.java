package com.openease.common.manager.task.data;

import com.openease.common.data.dao.log.EventLogDao;
import com.openease.common.data.model.log.EventLog;
import com.openease.common.manager.task.base.BaseTask;
import com.openease.common.util.spring.BeanLocator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Task: Create event log
 *
 * @author Alan Czajkowski
 */
public class CreateEventLogTask extends BaseTask<EventLog> {

  private static final transient Logger LOG = LogManager.getLogger(CreateEventLogTask.class);

  private EventLog eventLog;

  private EventLogDao eventLogDao;

  public CreateEventLogTask(EventLog eventLog) {
    this.eventLog = eventLog;
    this.eventLogDao = BeanLocator.byClass(EventLogDao.class);
  }

  @Override
  protected EventLog execute() {
    try {
      if (eventLog.getId() == null) {
        eventLog.setId();
      }
      eventLog = eventLogDao.save(eventLog);
    } catch (Exception e) {
      LOG.error(() -> ("[" + id + "] " + e.getMessage()), e);
    }
    return eventLog;
  }

  @Override
  protected void logStart() {
    LOG.info(this::getLogStartMessage);
  }

  @Override
  protected void logFinish() {
    LOG.info(this::getLogFinishMessage);
  }

}
