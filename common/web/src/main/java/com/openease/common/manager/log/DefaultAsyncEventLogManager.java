package com.openease.common.manager.log;

import com.openease.common.data.model.log.EventLog;
import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.task.data.CreateEventLogTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Future;

import static com.openease.common.util.JsonUtils.toJson;
import static com.openease.common.web.util.HttpUtils.getIpAddress;

/**
 * Default asynchronous event log manager
 *
 * @author Alan Czajkowski
 */
@Service
public class DefaultAsyncEventLogManager extends BaseEventLogManager {

  private static final transient Logger LOG = LogManager.getLogger(DefaultAsyncEventLogManager.class);

  @Autowired
  @Qualifier("defaultAsyncTaskExecutor")
  private AsyncTaskExecutor asyncTaskExecutor;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    super.init();
    LOG.debug("Init finished");
  }

  @Override
  public EventLog create(HttpServletRequest httpRequest, String description, String rawData, String accountId) throws GeneralManagerException {
    EventLog eventLog = null;

    if (enabled) {
      eventLog = new EventLog()
          .setId()
          .setIpAddress(getIpAddress(httpRequest))
          .setDescription(description)
          .setRawData(rawData)
          .setAccountId(accountId);
      LOG.trace("create({})", toJson(eventLog));
      CreateEventLogTask task = new CreateEventLogTask(eventLog);
      LOG.debug("Submitting {} with id: {}", () -> task.getClass().getSimpleName(), task::getId);
      //TODO: deal with this future somehow (since task may fail)
      Future<EventLog> taskFuture = asyncTaskExecutor.submit(task);
    } else {
      LOG.warn("{} disabled, ignoring create(..., \"{}\", ...)", EventLogManager.class::getSimpleName, () -> description);
    }

    return eventLog;
  }

}
